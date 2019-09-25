package services

import org.springframework.stereotype.Component
import wslite.http.auth.HTTPBasicAuthorization
import wslite.soap.SOAPClient
import wslite.soap.SOAPResponse

import javax.xml.ws.soap.SOAPFaultException

@Component
class Wso2UsersManager {
    private static final String NO_LIMITS = -1
    private
    final String SOAP_WSDL = PropertiesResolver.properties.'wso2.host' + 'services/UserAdmin?wsdl'
    private static final String WSO2_USER_NAME = 'admin'
    private static final String WSO2_PASSWORD = 'admin'
    private final SOAPClient client

    Wso2UsersManager() {
        client = new SOAPClient(SOAP_WSDL)
        client.authorization = new HTTPBasicAuthorization(WSO2_USER_NAME, WSO2_PASSWORD)
        // Trust the ssl for this site
        client.httpClient.sslTrustAllCerts = true
    }

    boolean userNotExists(String userName) {
        SOAPResponse response = client.send(SOAPAction: 'urn:listUsers') {
            body {
                addUser(xmlns: 'http://org.apache.axis2/xsd') {
                    filter(userName)
                    limit(NO_LIMITS)
                }
            }
        }
        response.body.listUsersResponse.return.collect { it }.isEmpty()
    }


    void createUser(String name, String userPassword, List<String> userRoles) {
        if (userNotExists(name)) {
            try {
                client.send(SOAPAction: 'urn:addUser') {
                    body {
                        addUser(xmlns: 'http://org.apache.axis2/xsd') {
                            userName(name)
                            password(userPassword)
                            userRoles.each { roles(it) }
                        }
                    }
                }
            } catch (SOAPFaultException e) {
                e.printStackTrace()
            }
        } else {
            println "User $name is already exists"
        }
    }

    void deleteRoles(String role) {
        client.send(SOAPAction: 'urn:deleteRole') {
            body {
                deleteRole(xmlns: 'http://org.apache.axis2/xsd') {
                    roleName(role)
                }
            }
        }
    }
}