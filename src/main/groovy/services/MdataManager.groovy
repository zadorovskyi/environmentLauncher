package services

import groovyx.net.http.HTTPBuilder
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.URLENC

@Component
class MdataManager {
    private
    final String OGC_AUTHORIZE_URI = PropertiesResolver.properties.'ogc.host2' + 'j_spring_security_check'
    private  final String GET_TOKEN_URL = PropertiesResolver.properties.'ogc.host2' + 'config/load'
    private  final String UPLOAD_MDATA_URI = PropertiesResolver.properties.'ogc.host2' + 'config/upload'
    private  final String MDATA_PATH = PropertiesResolver.properties.'path.to.testMdata'
    private static final String USER_NAME = 'super_a'
    private static final String USER_PASSWORD = 'supera123'
    private static final String TOKEN_FIELD_NAME = 'XMC_Token'

    private HTTPBuilder httpBuilder

    @PostConstruct
    init() {
        httpBuilder = new HTTPBuilder()
    }

    String getToken() {
        authorize()
        String html = httpBuilder.get(uri: GET_TOKEN_URL,
                                      contentType: TEXT) { resp ->
            return resp.getEntity().getContent().getText()
        }
        parseHtml(html)
    }

    void authorize() {
        httpBuilder.setUri(OGC_AUTHORIZE_URI)
        def postBody = [j_username: USER_NAME, j_password: USER_PASSWORD]
        httpBuilder.post(body: postBody, requestContentType: URLENC) { resp ->
            println "Authentication success: ${resp.statusLine}"
        }
    }

    String parseHtml(String html) {
        Document document = Jsoup.parse(html)
        Element element = document.getElementsByTag('input').find {
            return it.attr('name') == TOKEN_FIELD_NAME
        }
        element.attr('value')
    }

    void uploadMdata(String token) {
        File mdataFile
        HttpPost httpPost = null
        try {
            mdataFile = new File(MDATA_PATH)
            httpBuilder.setHeaders(['X-Requested-With': token])
            httpPost = new HttpPost(UPLOAD_MDATA_URI);
            FileBody bin = new FileBody(mdataFile)
            HttpEntity reqEntity = MultipartEntityBuilder.create()
                                                         .addPart('file', bin)
                                                         .addTextBody(TOKEN_FIELD_NAME, token)
                                                         .build()
            httpPost.setEntity(reqEntity)
            HttpResponse response = httpBuilder.getClient().execute(httpPost)
            if (response.statusLine.statusCode == 302) {
                println 'Mdata successfully uploaded'
            }
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection()
            }
        }
    }
}
