package com.hostel_online.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import ug.sparkpl.momoapi.network.RequestOptions;
import ug.sparkpl.momoapi.network.collections.CollectionsClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class requestTask extends AsyncTask
{
  CollectionsClient client;
  HashMap<String, String> collMap;
  HostelOnlineUser hostelOnlineUser;
  Room paidFor;
  WeakReference<Context> context;
  public requestTask(Context context, CollectionsClient client, HashMap<String, String> collMap, HostelOnlineUser hostelOnlineUser, Room paidFor)
  {
    this.context = new WeakReference<>(context);
    this.client = client;
    this.collMap = collMap;
    this.hostelOnlineUser = hostelOnlineUser;
    this.paidFor = paidFor;
  }

  @Override
  protected Object doInBackground(Object[] objects) {
    try {
      String transactionRef = client.requestToPay(collMap);
      System.out.println(transactionRef);
      System.out.println(client.getTransaction(transactionRef));

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void onPostExecute(Object result)
  {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> data  = new HashMap<>();
    data.put("HostelId", paidFor.getHostelId());
    data.put("Amount", collMap.get("amount"));
    data.put("RoomLabel", paidFor.getRoomLabel());
    db.collection("Users").document(hostelOnlineUser.getUserId()).update(data);
    hostelOnlineUser.setUserRoomLabel(paidFor.getRoomLabel());
    hostelOnlineUser.setUserHostelId(paidFor.getHostelId());
    Intent userIntent = new Intent((Activity)context.get(), User.class);
    userIntent.putExtra("HostelOnlineUser", hostelOnlineUser);
    context.get().startActivity(userIntent);
  }
}

public class HostelDetailsAndRoomList extends AppCompatActivity
{
  private ArrayList<String> suggestedRooms = new ArrayList<>();
  private String hostelId;
  private TextView tvName;
  private TextView tvDetailsHostelDescription;
  private TextView tvRoomsSelectedForYou;
  private RatingBar rbHostelRating;
  private RatingBar rbHostelPriceRating;
  private RatingBar rbHostelFreedomRating;
  private RatingBar rbHostelComfortRating;
  private ImageView ivDetailsImage;
  private ArrayList<Room> rooms;
  private HostelOnlineUser hostelOnlineUser;
  public static final int RC_MAKE_BOOKING_PAYMENT = 500;
  public static final int RC_MAKE_FINAL_PAYMENT = 700;
  public static final String MY_SECRET_API_KEY="c4c641b7ef304f1d962d11aadb439f16";
  public static final String MY_SECRET_SUBSCRIPTION_KEY="3d766fa471b84cf0a9393f15dbdada37";
  public static final String MYSECRET_USER_ID="99c5dabe-1571-4b4d-a133-df5d1a9d2d2e";
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_hostel_details_and_room_list);
    Intent receiveHostelDetailsIntent = getIntent();
    hostelOnlineUser = receiveHostelDetailsIntent.getParcelableExtra("HostelOnlineUser");
    if(hostelOnlineUser == null)
    {
      Intent mainActivity = new Intent(this, MainActivity.class);
      startActivity(mainActivity);
    }
    tvName = findViewById(R.id.details_hostel_name);
    tvDetailsHostelDescription = findViewById(R.id.details_hostel_description);
    tvRoomsSelectedForYou = findViewById(R.id.details_selected_for_you);
    rbHostelRating = findViewById(R.id.details_rating_bar);
    rbHostelPriceRating = findViewById(R.id.details_price_rating);
    rbHostelFreedomRating = findViewById(R.id.details_freedom_rating);
    rbHostelComfortRating = findViewById(R.id.details_comfort_rating);
    ivDetailsImage = findViewById(R.id.details_image_view);
    RecyclerView rvRoomsList = findViewById(R.id.rooms_list);
    Intent receiveHostelDetailsAndRoomListIntent = getIntent();
    suggestedRooms = receiveHostelDetailsAndRoomListIntent.getStringArrayListExtra("SuggestedRooms");
    hostelId = receiveHostelDetailsAndRoomListIntent.getStringExtra("HostelId");
    rooms = new ArrayList<>();
    if(hostelId != null && suggestedRooms != null)
    {
      FirebaseFirestore db = FirebaseFirestore.getInstance();
      DocumentReference df = db.collection("Hostels").document(hostelId);
      df.get().addOnCompleteListener(task -> {
        if(task.isSuccessful())
        {
          DocumentSnapshot doc = task.getResult();
          if(doc != null && doc.exists())
          {
            @SuppressWarnings("unchecked") Map<String, Map<String, Object>> hostelRooms = (Map<String, Map<String, Object>>)doc.get("Rooms");
            if(hostelRooms != null && doc.exists())
            {
              String hostelName = (String)doc.get("Name");
              String hostelImage = (String)doc.get("Image");
              String hostelDescription = (String)doc.get("Description");
              float hostelRating = doc.get("Rating") != null ? Float.parseFloat((String) Objects.requireNonNull(doc.get("Rating"))) : 0f;
              float hostelPriceRating = doc.get("PriceRating") != null ? Float.parseFloat((String) Objects.requireNonNull(doc.get("Rating"))) : 0f;
              float hostelFreedomRating = doc.get("FreedomRating") != null ? Float.parseFloat((String) Objects.requireNonNull(doc.get("Rating"))) : 0f;
              float hostelComfortRating = doc.get("ComfortRating") != null ? Float.parseFloat((String) Objects.requireNonNull(doc.get("Rating"))) : 0f;
              for(int i = 0; i < suggestedRooms.size(); i++)
              {
                String roomLabel = suggestedRooms.get(i);
                String roomType = (String) Objects.requireNonNull(hostelRooms.get(roomLabel)).get("RoomType");
                if(roomType == null || roomType.length() < 1)
                  roomType = "Single";
                String image;
                String price;
                String bookingFee;
                switch(roomType)
                {
                  case "Single":
                  {
                    image = (String)doc.get("SingleRoomImage");
                    price = (String)doc.get("SingleRoomPrice");
                    bookingFee = (String)doc.get("SingleRoomBookingFee");
                  }
                  break;
                  case "Double":
                  {
                    image = (String)doc.get("DoubleRoomImage");
                    price = (String)doc.get("DoubleRoomPrice");
                    bookingFee = (String)doc.get("DoubleRoomBookingFee");
                  }
                  break;
                  case "Tripple":
                  {
                    image = (String)doc.get("TrippleRoomImage");
                    price = (String)doc.get("TrippleRoomPrice");
                    bookingFee = (String)doc.get("TrippleRoomBookingFee");
                  }
                  break;
                  default:
                  {
                    price = "Unavailable";
                    bookingFee = "Unavailable";
                    image = null;
                  }
                  break;
                }
                rooms.add(new Room(roomLabel, roomType, image, hostelId, price, bookingFee));
              }
              tvName.setText(hostelName);
              tvDetailsHostelDescription.setText(hostelDescription);
              rbHostelRating.setRating(hostelRating);
              rbHostelPriceRating.setRating(hostelPriceRating);
              rbHostelFreedomRating.setRating(hostelFreedomRating);
              rbHostelComfortRating.setRating(hostelComfortRating);
              rvRoomsList.setAdapter(new RoomListAdapter(HostelDetailsAndRoomList.this, rooms));
              if(suggestedRooms.size() == 0)
              {
                tvRoomsSelectedForYou.setText(HostelDetailsAndRoomList.this.getResources().getString(R.string.no_available_rooms));
              }
              try {
                GlideApp.with(HostelDetailsAndRoomList.this).load(hostelImage).into(ivDetailsImage);
              }catch(Exception e){
                GlideApp.with(HostelDetailsAndRoomList.this).load(ResourcesCompat.getDrawable(HostelDetailsAndRoomList.this.getResources(), R.mipmap.empty, null)).into(ivDetailsImage);
              }
            }else{
              Toast.makeText(getApplicationContext(), "Failed to retrive hostel document.", Toast.LENGTH_LONG).show();
            }
          }else{
            Toast.makeText(getApplicationContext(), "Failed to retrive hostel document.", Toast.LENGTH_LONG).show();
          }
        }else{
          Toast.makeText(getApplicationContext(), "Failed to retrive hostel document.", Toast.LENGTH_LONG).show();
        }
      });
    }
    rvRoomsList.setAdapter(new RoomListAdapter(HostelDetailsAndRoomList.this, rooms));
  }

  public void hideListDetails(View view)
  {
    View detailsParentView = ((View)view.getParent()).findViewById(R.id.room_details);
    if(detailsParentView.getVisibility() == View.VISIBLE)
    {
      detailsParentView.setVisibility(View.GONE);
      ((ImageView)view).setImageResource(R.drawable.arrow_down);
    }else{
      detailsParentView.setVisibility(View.VISIBLE);
      ((ImageView)view).setImageResource(R.drawable.arrow_up);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == RESULT_OK)
    {
      switch(requestCode)
      {
        case RC_MAKE_BOOKING_PAYMENT:
        {
          String roomLabel = data.getStringExtra("RoomLabel");
          String MomoNumber = data.getStringExtra("MoMoNumber");
          Log.w("MoMo Number", MomoNumber);
          if(roomLabel != null && MomoNumber != null)
          {
            RequestOptions opts = RequestOptions.builder().setCollectionApiSecret(MY_SECRET_API_KEY).setCollectionPrimaryKey(MY_SECRET_SUBSCRIPTION_KEY).setCollectionUserId(MYSECRET_USER_ID).setTargetEnvironment("sandbox").build();
            Room paidFor = new Room();
            for(int i = 0; i < rooms.size(); i++)
            {
              if(rooms.get(i).getRoomLabel().equals(roomLabel))
                paidFor = rooms.get(i);
            }

            if(paidFor != null)
            {
              HashMap<String, String> collMap = new HashMap<>();
              collMap.put("amount", paidFor.getPrice());
              collMap.put("mobile", MomoNumber);
              collMap.put("externalId", "hostelOnline" + roomLabel);
              collMap.put("payeeNote", "HostelOnline");
              collMap.put("payerMessage", "Booking for room " + roomLabel);
              CollectionsClient client = new CollectionsClient(opts);
              requestTask tsk = new requestTask(HostelDetailsAndRoomList.this, client, collMap, hostelOnlineUser, paidFor);
              tsk.execute();
            }

          }
        }
        break;
        case RC_MAKE_FINAL_PAYMENT:
        {

        }
        break;
        default:
          break;
      }
    }
  }
}