# TRS_extraction
Extracting images from illwinter's TRS gamefiles

its a piece of code used to extract images from a sprite collection of format .TRS
That kind of file is used by games from ill winter : Dominion & Conquest of elysium

there's a map for the monsters name that allow renaming of files to include their "subgroup" as they seem to be used by the executable (see marlin works on steam forums for those)
files are saved in ARGB png format.

massive credits goes to nobless_oblige for his python script doing essentially the same. I just wanted to have my own version (couldn't run the python script)

the .jar should work as is

java -jar the_jar.jar

it should print the options


----------


entry_point.java contains the main method and read the command line

FileOpener.java is a helper class used to open a channel on a file and return a bytebuffer

Utils.java is another helper class used to play with bits

coe4_utils.java is an utility class that contains offset for the "subgroups" of monsters.trs from Conquest of elysium 4

TRS_decoder.java is the main dishe. There's a very long introduction comment to explain how to read the .TRS files. code should be clear. 

There's essentially 3 methods : one that chekcs the signature of the file, one that read the header of the file and one that read the sprites. There's 3 others methods : one simply convert pixel data into java "Color" objects, and 2 methods for reading packed or unpacked sprites inside the .trs.

constructor -> check signature -> read header -> read sprites ( -> read unpacked or read packed )

trs_unpacker.jar is a runnable jar from those files 
