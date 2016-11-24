# Beacon

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
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/Map_View.jpg" width="200">
   </div>
   
  * Search events on a list view
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/List_View.jpg" width="200">
   </div>

2. Create events and hangouts and make them private/public 

  * Make Public events that are visible to anyone nearby
  * Make Private events that are only visible to friends
  * Set Location and time
  * Set tags and related media
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/Create_Event_View.jpg" width="200">
   </div>
  

3. Search and Sort events

  * By keyphrase
  * By Name
  * By Distance
  * By Tags
  * By Relevance
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/Fav_View.jpg" width="200">
    <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/Empty_Fav_View.jpg" width="200">
   </div>

4. Post comments and photos on event pages

5. Favourite events and receive notifications for favourited events
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/Fav_View.jpg" width="200">
   </div>

6. Edit how large the search radius is for finding events, and whether or not you want notifications
   <div style="text-align:center">
   <img src="https://github.com/lsaccoz/Beacon/blob/master/res_demo/Settings_View.jpg" width="200">
   </div>
 
##Installation Guide (For Developpers)
In order to be able to fork off of this repository to make your own changes to Beacon, we highly suggest that you download and install  Android Studio as your IDE for developement. If you do, the process of getting the Beacon's source code onto your own local machine should be relatively seamless and straightforward. 

###Dependancies
1. Beacon utilizes Google's Firebase for all the following services:
..* Remote Realtime Database
..* Storage of images and videos uploaded by the user
..* Sign-in Authentication (Via Google Sign-in only)
 
 The most notable of these is the Remote Realtime Database. It is this databse which contains all of the key information for that the 
 app uses such as: Users, Events, Comments, as well as references to the url locations of the aforementioned images and videos.
 


