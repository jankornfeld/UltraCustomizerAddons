# Description
Read a .txt file.<br>
For each line it sends a message to the Player and waits a specific amount of time for the next message.

# How to
Let's assume you have the following .txt file:
```text
Hello Traveler; 2
The inn is right this way; 4
Make sure to speak with Barbara
```
The first line will be sent immediately to the player. After that the script will wait for 2 seconds before sending the next line.
<br>
After the 2 seconds it will send the second line to the player and wait for 4 seconds after which it will send the next line and execute the next element in line.

It's important so add a ``;`` after the line to add a delay
