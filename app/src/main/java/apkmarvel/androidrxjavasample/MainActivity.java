package apkmarvel.androidrxjavasample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    /*credit to http://code.tutsplus.com/tutorials/getting-started-with-reactivex-on-android--cms-24387*/
    public static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Observable class has many static methods, called operators, to create Observable objects.*/
        Observable<String> myObservable = Observable.just("Hello"); // Emits "Hello"

        /*The observable we just created will emit its data only when it has at least one observer. */
        Observer<String> myObserver = new Observer<String>() {
            @Override
            public void onCompleted() {
                // Called when the observable has no more data to emit
                Log.e(TAG,"onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                // Called when the observable encounters an error
                Log.e(TAG,"onError");
            }

            @Override
            public void onNext(String s) {
                // Called each time the observable emits data
                Log.e(TAG, s);
            }
        };
       Subscription mySubscription = myObservable.subscribe(myObserver);
        /*To detach an observer from its observable while the observable is still emitting data,
         you can call the unsubscribe method on the Subscription object.*/
        mySubscription.unsubscribe();


        /*emits items from an array of Integer objects. To do so, you have to use the from operator, which can generate an Observable from arrays and lists.*/
        Observable<Integer> myArrayObservable  = Observable.from(new Integer[]{1, 2, 3, 4, 5, 6}); // Emits each item of the array, one at a time
        myArrayObservable.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer i) {
                Log.e(TAG, String.valueOf(i)); // Prints the number received
            }
        });
        /*Using the subscribeOn and observeOn operators, you can explicitly specify which thread should run the background job and which thread should handle the user interface updates.*/
        Observable<String> fetchFromGoogle = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    String data = fetchData("http://www.google.com");
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                }catch(Exception e){
                    subscriber.onError(e); // In case there are network errors
                }
            }
        });
        fetchFromGoogle
                .subscribeOn(Schedulers.newThread()) // Create a new Thread
                .observeOn(AndroidSchedulers.mainThread()) // Use the UI thread
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, s); // Prints the number received
                    }
                });
    }

    private String fetchData(String s) {
        return "sample data from :"+s;
    }
}
