/**
 * 
 * @author Corentin
 * If you are using the data extracted from the executable provided by Marlin (see steam forums) then you will notice that monsters have their sprites id in groups.
 * I thought it would be useful to find what those "subgroups" meant, and ended up with that list.
 * The idea is that monsters are grouped into "civilizations".
 * there also are sprites that are unused and a weird case for subgroup 11 where two different creature use the same image.  
 */
public class coe4_utils {
	public static String coe4_monster_nameFile(int sprite_index)
	{
		for(int i = 0; i < groups_offsets.length; i++)
		{
			if ( sprite_index < groups_offsets[i])
			{
				return "group_" + (i-1) + "_num_" + (sprite_index-groups_offsets[i-1]);
			}
		}
		return null;
	}
	//index are group number, value in array at that index is the index of image 0 of that group.
	//ie image 0 and 1 of group 5 is at index groups_offsets[5] and groups_offsets[5]+1
	//ie image 56 of group 6 is at index groups_offsets[6]+56
	static int groups_offsets[] = {
			0, 99, 198, 231, 280, 341, 356, 407, 474, 541, 710, 771, 917, 1031, 1151,1187,1198,1307,1414,1479,1489,1574,1664,1738,1817,1482,1997,2130,2187,2241,2396,2449,2512,
			99999999
	};
	//99 = group 1 0	 // captain
	//198 = group 2 0	//catapult
	//231 = group 3 0	//barbarian leader
	//280 = group 4 0	//centurion
	//341 = group 5 0	//elephant & sphinx
	//356 = group 6 0	//paladin
	//407 = group 7 0	//large spider
	//474 = group 8 0	//imp messenger
	//541 = group 9 0	//longdead
	//710 = group 10 0	//terracotta (long spear) and gargoyle
		
	//767 = group 11 0	//child of catharsis, consider it unreferenced.
	//771 = group 11 0	//fire elemental, considered group 11 too :/
		//here
	//908 = group 12 //unreferenced
	//917 = group 12 (black cat) considered group 12 too

	//1028 = group 13 0 //unreferenced
	//1031 = group 13 0 //wolf


	//1151 = group 14 //woodman
	//1187 = group 15 0 //totems
	//1198 = group 16 0 //maker of ruins
	//1307 = group 17 0 //dvala

	//1414 = group 20 0 //goblin murderer !
	//1479 = group 21 0 // un dragon ! je viens de voir un dragon !
	//1489 = group 22 0 //pale one
	//1574 = group 23 0 //dai bakemono
	//1661 = group 24 0 //hoburg militia
	//1738 = group 25 0 // king
	//1817 = group 26 0 //crystal sorceress
	//1842 = group 27 0 //human wizards  
	//1997 = group 28 0 //king priests units
	//2130 = group 29 0 //hybrid cultist
	//2187 = group 30 0 //no reference used, 
	//2241 = group 31 0 //large mirror
	//2396 = group 32 0 //stuff in the sea
	//2449 = group 33 0 //hoburg undead
	//2512 = group 34 0 //centaurs and dryad stuff
}
