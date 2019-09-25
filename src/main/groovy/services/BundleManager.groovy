package services

import groovyx.net.http.HTTPBuilder
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

import static groovyx.net.http.ContentType.JSON

@Component
class BundleManager {
    private
    final String path = PropertiesResolver.properties.'path.to.testBundle'
    private static final String FILE_NAME = 'install_file'
    private
    final String UPLOAD_BUNDLE_URI = PropertiesResolver.properties.'omniConsole.host' + 'deploy/bundle/upload?clean=true'
    private
    final String RESTART_SERVICES_URI = PropertiesResolver.properties.'omniConsole.host' + 'service/start/all'

    private HTTPBuilder builder

    @PostConstruct
    private init() {
        builder = new HTTPBuilder()
        builder.ignoreSSLIssues()
    }

    void uploadBundle() {
        File bundleFile
        HttpPost httpPost = null
        try {
            bundleFile = new File(path)
            httpPost = new HttpPost(UPLOAD_BUNDLE_URI)
            FileBody fileBody = new FileBody(bundleFile)
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart(FILE_NAME, fileBody).build()
            httpPost.setEntity(reqEntity)
            HttpResponse response = builder.getClient().execute(httpPost)
            if (response.statusLine.statusCode == 200) {
                println 'Bundle successfully uploaded'
            }
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection()
            }
        }
    }

    void startServices() {
        builder.get(uri: RESTART_SERVICES_URI, contentType: JSON) { response ->
            if (response.statusLine.statusCode == 200) {
                println 'Services successfully started'
            }
        }
    }
}
