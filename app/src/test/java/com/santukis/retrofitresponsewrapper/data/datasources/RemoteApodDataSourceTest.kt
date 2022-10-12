package com.santukis.retrofitresponsewrapper.data.datasources

import com.santukis.retrofitresponsewrapper.data.entities.ServerErrorDto
import com.santukis.retrofitresponsewrapper.data.remote.CustomException
import com.santukis.retrofitresponsewrapper.data.remote.HttpClient
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import java.util.*

internal class RemoteApodDataSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apodDataSource: ApodDataSource

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        val client = HttpClient(mockWebServer.url("").toString())

        apodDataSource = RemoteApodDataSource(client)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `loadApod should parse errorBody successfully`() {
        runBlocking {
            val errorBody = """
                {
                    "error": {
                        "code": "API_KEY_MISSING",
                        "message": "No api_key was supplied. Get one at https://api.nasa.gov:443"
                    }
                }
            """.trimIndent()

            mockWebServer.enqueue(MockResponse().setResponseCode(422).setBody(errorBody))

            apodDataSource.loadApod(Date())
                .onSuccess { fail("Failure should be called") }
                .onFailure {
                    assertTrue(it is CustomException)
                    assertTrue((it as CustomException).error is ServerErrorDto)
                    assertEquals("API_KEY_MISSING", (it.error as ServerErrorDto).error?.code)
                    assertEquals("No api_key was supplied. Get one at https://api.nasa.gov:443", (it.error as ServerErrorDto).error?.message)
                }

        }
    }

    @Test
    fun `loadApod should return exception when errorBody is empty`() {
        runBlocking {
            mockWebServer.enqueue(MockResponse()
                .setResponseCode(404)
            )

            apodDataSource.loadApod(Date())
                .onSuccess { fail("Failure should be called") }
                .onFailure {
                    assertTrue(it is HttpException)
                }

        }
    }

    @Test
    fun `loadApod should parse body successfully`() {
        runBlocking {
            val body = """
                {
                    "copyright": "Johan Bogaerts",
                    "explanation": "The North America nebula on the sky can do what the North America continent on Earth cannot",
                    "media_type": "image",
                    "service_version": "v1",
                    "title": "The Cygnus Wall of Star Formation",
                    "url": "https://apod.nasa.gov/apod/image/2208/CygnusWall_Bogaerts_960.jpg"
                }
            """.trimIndent()

            mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(body))

            apodDataSource.loadApod(Date())
                .onSuccess {
                    assertEquals("Johan Bogaerts", it.copyright)
                    assertEquals("The North America nebula on the sky can do what the North America continent on Earth cannot", it.description)
                    assertEquals("https://apod.nasa.gov/apod/image/2208/CygnusWall_Bogaerts_960.jpg", it.url)
                }
                .onFailure { fail("Failure should be called") }

        }
    }

    @Test
    fun `loadApod2 should parse errorBody successfully`() {
        runBlocking {
            val errorBody = """
                [
                    {
                        "error": {
                            "code": "Error1",
                            "message": "message1"
                        }
                    },
                    {
                        "error": {
                            "code": "Error2",
                            "message": "message2"
                        }
                    }
                ]
            """.trimIndent()

            mockWebServer.enqueue(MockResponse().setResponseCode(422).setBody(errorBody))

            apodDataSource.loadApod2(Date())
                .onSuccess { fail("Failure should be called") }
                .onFailure { exception ->
                    (exception as? CustomException)?.let { customException ->
                        (customException.error as? List<*>)?.firstOrNull()?.let { serverErrorDto ->
                            (serverErrorDto as? ServerErrorDto)?.let { error ->
                                assertEquals("Error1", error.error?.code)
                                assertEquals("message1", error.error?.message)
                            }
                        }
                    } ?: fail("Exception is not as expected")
                }

        }
    }
}