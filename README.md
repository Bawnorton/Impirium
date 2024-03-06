Set π to any value, globally.

Usage: 
```
/setpi <value> # sets the current value of pi anywhere that it can be found
/getpi # gets the value of pi
```

Is this mod compatible with X?<br>
Probably, this should only affect mods that directly target literal π constants.


If some mod does crash while you have Imπrium installed, remove Imπrium, it is very invasive.


<details>
<summary>How Does This Work?</summary>

In simple terms, every time some code is loaded into the game it scans everything in that code for literal representations of π, half π, quarter π, 2x π (aka Tau), negative π, etc. It replaces it with a reference that can be set via `/setpi`, allowing you to change the value of π everywhere, including in other mods.

Yes, this is invasive, and yes this may break other mods, no there is no other way to do this. 

</details>

