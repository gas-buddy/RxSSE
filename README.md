# RxSSE
RxSSE is a Retrofit call adapter factory that emits Server Sent Events streamed as RxJava observables.

## How to use it?
#### Define a `MessageProcessor`:
A `MessageProcessor` is an interface that you must implement.  This class is responsible for converting the raw text data sent in an SSE data element into a concrete class.  The data element may be json, xml, or any other structure you choose.

For example, a simple Gson message processor might be
```
class GsonMessageProcessor: MessageProcessor {

    companion object {
        private val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create()
    }

    override fun <T> processMessage(message: String, type: Type): T {
        return gson.fromJson<T>(message, type)
    }
}
```

#### Create the call adapter factory:
```
val api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJavaSseCallAdapterFactory(
                    scheduler = Schedulers.io(),
                    messageProcessor = MyGsonMessageProcessor()
            ))
            .build()
```

#### Define your Retrofit API:
```
@Headers("Content-Type:application/json", "Accept:text/event-stream", "Cache-Control:no-cache")
@POST("/example/thing")
@Streaming
@JvmSuppressWildcards
fun postSomethingNeat(
  @Body details: PostDetails
) : Observable<NeatResponse>
```

#### Use the Retrofit API:
```
api.postSomethingNeat(PostDetails(...))
    .subscribe {
      neatResponse ->
      Log.d(TAG, "$neatResponse received onNext!")
    }

```

## How to get it?
[ ![Download](https://api.bintray.com/packages/mferguson/maven/com.gasbuddy.mobile:rxsse/images/download.svg?version=0.9) ](https://bintray.com/mferguson/maven/com.gasbuddy.mobile:rxsse/0.9/link)

RxSSE is hosted on jcenter.  You can import it into your project by adding the following to your gradle dependencies:
```
implementation 'com.gasbuddy.mobile:rxsse:0.9'
```
