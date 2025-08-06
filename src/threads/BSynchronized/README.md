### Learning Synchronized
Remember that this need to be applied where your code is being shared for multiple threads. a BIG confusion is try to apply on each thread instanciated. If you do this. You will be creating the same number of "synchronized" as the number of threads instanciated and will work independently one of each other.

Saying this make sure if the method/block of code where are you going to use the "synchronized". If this method is part of an object that will have multiple instances this is a first warning because you will be creating multiple "synchronizeds". Other option is have a static method to synchronize. since static methods are "unique" you will have only one synchronized
