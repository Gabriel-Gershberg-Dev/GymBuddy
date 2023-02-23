package com.example.gymbuddy.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymbuddy.R;
import com.example.gymbuddy.adapters.SwipeAdapter;
import com.example.gymbuddy.models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    CardStackView cardStackView;
    TextView errorTv;
    SwipeAdapter adapter;
    List<UserModel> list = new ArrayList<>();
    List<String> buddylist = new ArrayList<>();
    CardStackLayoutManager manager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        cardStackView = view.findViewById(R.id.cardStackView);
        errorTv = view.findViewById(R.id.errorTv);
        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {
                if (direction.equals(Direction.Right)){
                    Map map=new HashMap();
                    map.put("status","yes");
                    map.put("date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                  FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Buddies").document(list.get(manager.getTopPosition()-1).getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void unused) {
                          FirebaseFirestore.getInstance().collection("Users").document(list.get(manager.getTopPosition()-1).getUid()).collection("Buddies").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void unused) {

                              }
                          });
                      }
                  });
                }
                if (manager.getTopPosition()==list.size()){
                    errorTv.setVisibility(View.VISIBLE);
                }else {
                    errorTv.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });
        manager.setMaxDegree(20.0f);
        manager.setTranslationInterval(0.6f);
        manager.setScaleInterval(0.8f);
        manager.setDirections(Direction.HORIZONTAL);

        cardStackView.setLayoutManager(manager);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

      getData();

        return view;
    }

    //Getting the list of all Users from firestore and filtering the list to remove those users which are already in
    // my buddy list and  sending the list to adatpter to show users in cards matching view

    private void getData() {
        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Buddies").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.exists()) {
                        buddylist.add(documentSnapshot.getId());
                    }
                }

                FirebaseFirestore.getInstance().collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        list.clear();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (documentSnapshot.exists()) {
                                if (!documentSnapshot.toObject(UserModel.class).getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && !buddylist.contains(documentSnapshot.getId())) {
                                    list.add(documentSnapshot.toObject(UserModel.class));
                                }

                            }
                        }

                        //Shuffling the list so that every time user see different users at top

                        Collections.shuffle(list);

                        // setting the adapter.................................

                        adapter = new SwipeAdapter(getActivity(), list);
                        cardStackView.setAdapter(adapter);
                    }
                });

            }
        });
    }
//        public void permission1(){
//            Intent intent= new Intent();
//            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            startActivityForResult(intent,12);
//        }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 12 && resultCode == RESULT_OK) {
//            final ContentResolver resolver = requireActivity().getContentResolver();
//            resolver.takePersistableUriPermission(data.getData(),
//                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            getData();
//        }/*from   ww w.  j ava  2 s . co  m*/
//    }

}