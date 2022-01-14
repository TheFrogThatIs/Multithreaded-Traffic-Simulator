# Multithreaded-Traffic-Simulator
A basic, multi-threaded traffic simulator application written in Java. \
Technical Notes:
``` 
Distances - 1000m corresponds to 200px on screen
Speeds    - Exaggerated 10-fold for timing. Speed equation: x = x0 + v*t
Physics   - Cars come to immediate stop with no develeration at red lights. 
Threads   -
  1. GUIClock - Timer at top of screen
  2. TrafficLights - Clocks and traffic lights on screen
  3. CarsTraversing - Cars traversing the screen, subject to light conditions
```
**Usage (From Base Directory):**
```
  >javac com/Me/*.java
  >java -cp . com.Me.TrafficSimulator
```

![image](https://user-images.githubusercontent.com/92680247/148480502-fe09e9ce-411a-46ce-82da-da4e1350f9fc.png)

