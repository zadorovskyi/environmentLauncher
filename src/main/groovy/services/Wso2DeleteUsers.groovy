package services

import wslite.http.auth.HTTPBasicAuthorization
import wslite.soap.SOAPClient
import wslite.soap.SOAPResponse

import javax.xml.ws.soap.SOAPFaultException

class Wso2DeleteUsers {
    private static final String NO_LIMITS = -1
    private static
    final String SOAP_WSDL = 'https://toptop.softservecom.com:9443/services/UserAdmin?wsdl'
    private static final String WSO2_USER_NAME = 'admin'
    private static final String WSO2_PASSWORD = 'admin'
    private final SOAPClient client

    Wso2DeleteUsers() {
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

    def getRoles(String filter1) {
        def response = client.send(SOAPAction: 'urn:getAllRolesNames') {
            body {
                getAllRolesNames(xmlns: 'http://org.apache.axis2/xsd') {
                    filter(filter1)
                    limit(-1)
                }
            }
        }

        response.body.getAllRolesNamesResponse.return.
                collect { it.itemName.toString() } ?: []
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
