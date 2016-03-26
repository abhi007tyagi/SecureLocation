# SecureLocation
This application demonstrate, how a location can be identified using BLE based beacons and implement organization policies on the device to make it secure. In this demo, if a user enters a secured location (beacon is detected), then camera of the device is disabled. Once the user exits the secure location, camera is enabled automatically. This use case can be enhanced to automatically activate different policies and settings on devices when in specific location. This use case can also be used for other purposes like indoor beacon based navigation, announcements, advertisments, etc.

<br>All rights reserved. No part of this project may be reproduced, distributed,copied,transmitted or
        transformed in any form or by any means, without the prior written permission of the developer.
        For permission requests,write to the developer,addressed “Attention:Permissions Coordinator,”
        at the address below.

        Abhinav Tyagi
        DGIII-44Vikas Puri,
        New Delhi-110018
        abhi007tyagi@gmail.com

<br> <br><b>Prepare beacon</b>

1. Get Nexus6 device or any other device which supports BLE broadcasting.<br>
2. Install QuickBeacon app from Radius Networks.<br>
3. Enable bluetooth of the device<br>
4. Open the app and put following settings<br> 
   i. ID1: 43A2BC29C1114A768B6F78AECB142E5A (this is UUID for beacon, you can use your own UUID) <br>
   ii. ID2:1(optional... major)  <br>
   iii. ID3:7(optional... minor)  <br>
   iv. Power: -56  <br>
   v. Advertisements Per Second: 10 (Low Latency)  <br>
   vi. Transmitter Power: High or Medium  <br>
   vii. Beacon format: AltBeacon  <br>
5. Switch ON and select "Apply" <br>

Your beacon is ready!! <br>

<br><b>User Device that is to be secured</b> <br>
1. Get Nexus5 or any android device that supports BLE. <br>
2. Install SecureLocation app. <br>
3. Run the app. <br>
4. Select "Activate" to provide Device Admin features to app to control device camera. <br>
5. Select "Allow" to enable bluetooth on device. <br>
6. Make sure UUID field has same ID as in Beacon (Nexus6) i.e. "43A2BC29C1114A768B6F78AECB142E5A" if using default. <br>
7. Use default settings for Scan Time and Scan Intervals i.e. 5 sec and 1 min respectively. <br>
8. Use Altbeacon if beacon is prepared using Altbeacon standard or iBeacon if beacon prepared via iBeacon standard. <br>
9. Start the background service by sliding the switch to right.<br>

Application is ready to sense beacon with above UUID and hense detect secure location to disable/enable camera.<br>

Now whenever the app senses beacon, it will disable device camera and user will not be able to use camera. Once user is outside secure location, application will enable the device camera.<br>

Similarly, other organization policies can be implemented.
