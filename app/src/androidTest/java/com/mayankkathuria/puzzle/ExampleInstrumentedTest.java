package com.mayankkathuria.puzzle;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private  FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference puzzleRef = firebaseDatabase.getReference("puzzle");
    private DatabaseReference size3Ref = firebaseDatabase.getReference("puzzle/size3");
    private DatabaseReference size3HoursRef = firebaseDatabase.getReference
                                              ("puzzle/size3/LowestHours");
    private DatabaseReference size3MinutesRef = firebaseDatabase.getReference
                                                ("puzzle/size3/LowestMinutes");
    private DatabaseReference size3SecondsRef = firebaseDatabase.getReference
                                                ("puzzle/size3/LowestSeconds");
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private int numOfChildren;

    /**
     *
     * @throws Exception if the test fails
     * tests for the number children of children of the puzzle reference
     */
    @Test
    public void puzzleTest() throws Exception{

        numOfChildren = 0;
        puzzleRef.addChildEventListener(new ChildEventListener() {

            boolean failed = false;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                numOfChildren++;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                numOfChildren--;
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                failed = true;
            }
        });
        countDownLatch.await(1, TimeUnit.SECONDS);
        assertEquals(2, numOfChildren);
    }

    /**
     *
     * @throws Exception if the test fails
     * tests for the number children of children of the size3 reference
     */
    @Test
    public void size3Test() throws Exception{

        numOfChildren = 0;
        size3Ref.addChildEventListener(new ChildEventListener() {

            boolean failed = false;
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                numOfChildren++;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                numOfChildren--;
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                failed = true;
            }
        });
        countDownLatch.await(1, TimeUnit.SECONDS);
        assertEquals(3, numOfChildren);
    }

    /**
     *
     * @throws Exception if the test fails
     * tests for writing the data on Firebase
     */
    @Test
    public void writeTest() throws Exception{

        size3HoursRef.setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                countDownLatch.countDown();
            }
        });

        size3MinutesRef.setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                countDownLatch.countDown();
            }
        });

        size3SecondsRef.setValue(10).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                countDownLatch.countDown();
            }
        });


        countDownLatch.await(1, TimeUnit.SECONDS);
    }

    /**
     *
     * @throws Exception if the test fails
     * tests for reading the data from Firebase
     */
    @Test
    public void readTest() throws Exception{
        size3HoursRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                assertTrue(0 == dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

        size3MinutesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                assertTrue(0 == dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

        size3SecondsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                assertTrue(10 == dataSnapshot.getValue(Integer.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
        countDownLatch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.mayankkathuria.puzzle", appContext.getPackageName());
    }
}
