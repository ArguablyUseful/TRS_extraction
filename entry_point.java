import java.io.IOException;

public class entry_point {

	public static void main(String args[]) throws IOException
	{
		if ( args.length < 1)
		{
			String str = "";
			str += "java runnable.jar TRS_FILE_PATH [--coe4_monsters_subgroups] [--type_naming]\n";
			str += "TRS_FILE_PATH must be a valid .trs file\n";
			str += "--coe4_monsters_subgroups is useful when unpacking monster.trs from CoE4, it will create filename with subgroups used by the game\n";
			str += "--type_naming will append a \"_u\" when the file was unpacked and \"_p\" when the file was packed. unpacked sprites have a black background instead of transparent.";
			System.out.println(str);
		}
		else
		{
			boolean flag_monster_subgroups = false;
			boolean flag_packed = false;
			String option_feedback = "";
			if ( args.length > 1)
				if ( args[1].equals("--coe4_monsters_subgroups"))
				{
					flag_monster_subgroups = true;
					
				}
			if ( args.length > 2)
				if ( args[2].equals("--type_naming"))
				{
					flag_packed = true;
				}
			if ( flag_monster_subgroups)
				option_feedback += "CoE4 monsters subgroup naming is ACTIVE\n";
			else
				option_feedback += "CoE4 monsters subgroup naming is INACTIVE\n";
			if ( flag_packed)
				option_feedback += "type naming is ACTIVE\n";
			else
				option_feedback += "type naming is INACTIVE\n";
			System.out.println("Unpacking file named " + args[0]);
			System.out.println(option_feedback);
			new TRS_decoder(args[0],flag_monster_subgroups, flag_packed);
			System.out.println("Done.");
		}
							
	}
}
