#Beacon
*Shining a Light on the Action*

##Motivation/Opportunity

People have a limited ability to stay connected to social events going on around them. 
There is no seamless way to see what events are currently going on near your current location. 
Additionally, there is no way for small event planners to advertise to potential attendees in their area, while their event is occurring. 
BEACON solves this problem by allowing users to pin their event location and information to a shared map that all users can view in real-time. 
This app will separate itself from other platforms, such as the event planning system offered by Facebook, by not focusing as much on the planning aspect of social events. 
Instead, BEACON will try and encourage on-the-fly decision making, by focusing on nearby events that are occurring at the moment. 

##Features

1. Find events and hangouts occurring within a local vicinity

  * Search events on a map view 
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/world_view.png" width="200">
   </div>
   
  * Search events on a list view
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/list_view.png" width="200">
   </div>

2. Create events and hangouts and make them public 

  * Make events that are visible to anyone nearby
  * Set name and description
  * Set location
  * Set date and time
  * Set event photos
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/create_event_page.jpg" width="200">
   </div>

3. Search and Sort events

  * By Name
  * By Description
  * By Distance
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/search_feature.png" width="200">
   </div>

4. Post comments and ask questions on event pages

5. Favourite events to remember events of interest
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/favourites_page.png" width="200">
   </div>

6. Edit how large the search radius is for finding events
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/settings_page.png" width="200">
   </div>

##Installation Guide (For Users)
If you wish to become a tester for the initial alpha release of Beacon on the Google Play Store, follow the link bellow to install Beacon on your android device:

https://play.google.com/apps/testing/com.bcn.beacon.beacon

##Installation Guide (For Developers)
In order to be able to fork off of this repository to make your own changes to Beacon, we highly suggest that you download and install  Android Studio as your IDE for developement. If you do, the process of getting the Beacon's source code onto your own local machine should be relatively seamless and straightforward. The following steps describe how to install Beacon.

###Dependencies
1. Beacon utilizes Google's Firebase for all the following services:

  * Remote Realtime Database
  * Storage of images and videos uploaded by the user
  * Sign-in Authentication (Via Google Sign-in only)
 
 The most notable of these is the Remote Realtime Database. It is this database which contains all of the key information that the a app uses, such as: Users, Events, Comments, as well as references to the URL locations of the aforementioned images and videos. 

 To learn how to use the Firebase Realtime Database effectively, an extensive compilation of resources, as well as documentation can be found at: https://firebase.google.com/docs/android/setup

2. Google Maps API and Google Places API

 Beacon uses the Google Maps and the Google Places APIs in order to effectively and efficiently display all the user generated events on a map which the user can easily interact with. Not only does Google Maps allow the user to see the events going around them at any given point in time, it is also how the user sets the location of their event when they wish to create one themselves. Beacon uses Google Places in order to autocomplete/suggest locations and addresses to the user as they are inputting the location of their event.
 
 If you are looking to learn more about the Google Maps API, resources and documentation can be found at: https://developers.google.com/maps/documentation/android-api/start
 
###Getting Beacon on your Local Machine
Getting off the ground with your personal development of Beacon couldn't be simpler. All you'll need to do is download Android Studio (as mentioned before, this is the IDE we recommend), and start developing! You can clone this repository directly into AndroidStudioProjects directory for the simplest import into Android Studio. Then, after the cloning is complete, all you have to do is open Android Studio and navigate to "File->New->Import Project" and select the cloned repository. After that, you should be able to see the entire Beacon project.

Since Beacon makes use of the Google services mentioned above, the app needed to have a Java Keystore that was synced with Firebase. We have conveniently provided the .jks file needed to compile and build the app. Settings for the .jks compilation can be found in the app level .gradle file.

If you have received a copy of a valid keystore you are ready to start contributing to Beacon! While following these steps, once again, please feel free to contact andy.terzakian@gmail.com for assistance.
