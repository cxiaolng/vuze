/*
 * Written and copyright 2001-2004 Tobias Minich. Distributed under the GNU
 * General Public License; see the README file. This code comes with NO
 * WARRANTY.
 * 
 * Hack.java
 * 
 * Created on 22.03.2004
 *
 */
package org.gudy.azureus2.ui.console.commands;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.gudy.azureus2.core3.disk.DiskManager;
import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.tracker.client.TRTrackerClient;
import org.gudy.azureus2.ui.console.ConsoleInput;

/**
 * @author Tobias Minich
 */
public class Hack extends TorrentCommand {
	
	public Hack() 
	{
		super(new String[] { "hack", "#" }, "Hacking");
		addSubCommand(new HackFile());
		addSubCommand(new HackTracker());
	}
	
	public String getCommandDescriptions()
	{
		return "hack [<various options>]\t#\tModify torrent settings. Use without parameters for further help.";
	}
	
	public void printHelp(PrintStream out, List args) {
		out.println("> -----");
		out.println("'hack' syntax:");
		if( args.size() > 0 ) {
			String command = (String) args.remove(0);
			TorrentCommand cmd = getSubCommand(command);
			if( cmd != null )
				cmd.printHelp(out, args);
			return;
		}
		out.println("hack <torrent id> <command> <command options>");
		out.println();
		out.println("<torrent id> can be one of the following:");
		out.println("<#>\t\tNumber of a torrent. You have to use 'show torrents' first as the number is taken from there.");
		out.println("hash <hash>\tApplied to torrent with the hash <hash> as given in the xml output or extended torrent info ('show <#>').");
		out.println("help\t\tDetailed help for <command>");
		out.println();
		out.println("Available <command>s:");
		for (Iterator iter = getSubCommands().iterator(); iter.hasNext();) {
			TorrentSubCommand cmd = (TorrentSubCommand) iter.next();
			out.println(cmd.getCommandDescriptions());
		}
		out.println("> -----");
	}

	/**
	 * finds the appropriate subcommand and executes it.
	 * the execute() method will have taken care of finding/iterating over the
	 * appropriate torrents
	 */
	protected boolean performCommand(ConsoleInput ci, DownloadManager dm,
			List args) 
	{
		if (args.isEmpty()) {
			ci.out.println("> Not enough parameters for command '" + getCommandName() + "'.");
			return false;
		}
		String subCommandName = (String)args.remove(0);
		TorrentSubCommand cmd = getSubCommand(subCommandName);
		if( cmd != null )
			return cmd.performCommand(ci, dm, args);
		else
		{
			ci.out.println("> Command 'hack': Command parameter '" + subCommandName + "' unknown.");
			return false;
		}
	}
	
	private static class HackTracker extends TorrentSubCommand
	{
		public HackTracker()
		{
			super(new String[] { "tracker", "t" });
			addSubCommand(new HackHost());
			addSubCommand(new HackPort());
			addSubCommand(new HackURL());
		}

		public void printHelp(PrintStream out, List args)
		{
			out.println("hack <torrent id> tracker [command] <new value>");
			out.println();
			out.println("[command] can be one of the following:");
			for (Iterator iter = getSubCommands().iterator(); iter.hasNext();) {
				TorrentSubCommand cmd = (TorrentSubCommand) iter.next();
				out.println(cmd.getCommandDescriptions());
			}
			out.println();
			out.println("You can also omit [command] and only give a new full URL (just like the [command] 'url').");
			out.println("> -----");
		}
		
		/**
		 * locate the appropriate subcommand and execute it 
		 */
		public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) 
		{
			if (args.isEmpty()) {
				ci.out.println("> Command 'hack': Not enough parameters for subcommand '" + getCommandName() + "'");
				return false;
			}
			String trackercommand = (String) args.remove(0);
			TRTrackerClient client = dm.getTrackerClient();
			//ci.out.println("> Command 'hack': Debug: '"+trackercommand+"'");
			if (client == null) {
				ci.out.println("> Command 'hack': Tracker interface not available.");
				return false;
			}
			TorrentSubCommand cmd = getSubCommand(trackercommand);
			if( cmd == null )
			{
				args.add(trackercommand);
				cmd = getSubCommand("url");
			}
			
			return cmd.performCommand(ci, dm, args);
		}

		public String getCommandDescriptions() {
			return "tracker\t\tt\tModify Tracker URL of a torrent.";
		}
	}
	
	private static class HackFile extends TorrentSubCommand
	{
		public HackFile()
		{
			super(new String[] { "file", "f" });
		}
		public void printHelp(PrintStream out, List args)
		{
			out.println("hack <torrent id> file <#> <priority>");
			out.println();
			out.println("<#> Number of the file.");
			out.println();
			out.println("<priority> can be one of the following:");
			out.println("normal\t\tn\tNormal Priority");
			out.println("high\t\th|+\tHigh Priority");
			out.println("nodownload\t!|-\tDon't download this file.");
			out.println("> -----");
		}
		public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) 
		{
			if (args.size() < 2) {
				ci.out.println("> Command 'hack': Not enough parameters for subcommand 'file'.");
				return false;
			}
			try {
				DiskManager disk = dm.getDiskManager();
				DiskManagerFileInfo files[] = disk.getFiles();
				int file = Integer.parseInt((String) args.get(0));
				String c = (String) args.get(1);
				if (c.equalsIgnoreCase("normal") || c.equalsIgnoreCase("n")) {
					files[file - 1].setSkipped(false);
					files[file - 1].setPriority(false);
					ci.out.println("> Set file '"+files[file - 1].getName()+"' to normal priority.");
				} else if (c.equalsIgnoreCase("high") || c.equalsIgnoreCase("h") || c.equalsIgnoreCase("+")) {
					files[file - 1].setSkipped(false);
					files[file - 1].setPriority(true);
					ci.out.println("> Set file '"+files[file - 1].getName()+"' to high priority.");
				} else if (c.equalsIgnoreCase("nodownload") || c.equalsIgnoreCase("!") || c.equalsIgnoreCase("-")) {
					files[file - 1].setSkipped(true);
					files[file - 1].setPriority(false);
					ci.out.println("> Stopped to download file '"+files[file - 1].getName()+"'.");
				} else {
					ci.out.println("> Command 'hack': Unknown priority '" + c + "' for command parameter 'file'.");
					return false;
				}
				return true;
			} catch (Exception e) {
				ci.out.println("> Command 'hack': Exception while executing subcommand 'file': " + e.getMessage());
				return false;
			}
		}

		public String getCommandDescriptions() {
			return "file\t\tf\tModify priority of a single file of a batch torrent.";
		}
	}
	
	private static class HackPort extends TorrentSubCommand
	{
		public HackPort()
		{
			super(new String[] { "port", "p" });
		}
		public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) 
		{
			if (args.isEmpty()) {
				ci.out.println("> Command 'hack': Not enough parameters for subcommand parameter 'port'.");
				return false;
			}
			TRTrackerClient client = dm.getTrackerClient();
			try {
				URI uold = new URI(client.getTrackerUrl().toString());
				String portStr = (String) args.get(0);
				URI unew = new URI(uold.getScheme(), uold.getUserInfo(), uold.getHost(), Integer.parseInt(portStr), uold.getPath(), uold.getQuery(), uold.getFragment());
				client.setTrackerUrl(new URL(unew.toString()));
				ci.out.println("> Set Tracker URL for '"+dm.getTorrentSaveDirAndFile()+"' to '"+unew.toString()+"'");
			} catch (Exception e) {
				ci.out.println("> Command 'hack': Assembling new tracker url failed: "+e.getMessage());
				return false;
			}
			return true;
		}
		public String getCommandDescriptions() {
			return "port\t\tp\tChange the port.";
		}
	}
	private static class HackHost extends TorrentSubCommand
	{
		public HackHost()
		{
			super(new String[] { "host", "h" });
		}
		public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) 
		{
			if (args.isEmpty()) {
				ci.out.println("> Command 'hack': Not enough parameters for subcommand parameter 'host'.");
				return false;
			}
			TRTrackerClient client = dm.getTrackerClient();
			try {
				URI uold = new URI(client.getTrackerUrl().toString());
				URI unew = new URI(uold.getScheme(), uold.getUserInfo(), (String)args.get(0), uold.getPort(), uold.getPath(), uold.getQuery(), uold.getFragment());
				client.setTrackerUrl(new URL(unew.toString()));
				ci.out.println("> Set Tracker URL for '"+dm.getTorrentSaveDirAndFile()+"' to '"+unew.toString()+"'");
			} catch (Exception e) {
				ci.out.println("> Command 'hack': Assembling new tracker url failed: "+e.getMessage());
				return false;
			}
			return true;
		}
		public String getCommandDescriptions() {
			return "host\t\th\tChange the host.";
		}
	}
	private static class HackURL extends TorrentSubCommand
	{
		public HackURL()
		{
			super(new String[] { "url", "u" });
		}
		public boolean performCommand(ConsoleInput ci, DownloadManager dm, List args) 
		{
			if (args.isEmpty()) {
				ci.out.println("> Command 'hack': Not enough parameters for subcommand parameter 'url'.");
				return false;
			}
			TRTrackerClient client = dm.getTrackerClient();
			
			try {
				String uriStr = (String) args.get(0); 
				URI uri = new URI(uriStr);
				client.setTrackerUrl(new URL(uri.toString()));
				ci.out.println("> Set Tracker URL for '"+dm.getTorrentSaveDirAndFile()+"' to '"+uri+"'");
			} catch (Exception e) {
				ci.out.println("> Command 'hack': Parsing tracker url failed: "+e.getMessage());
				return false;
			}
			return true;
		}
		public String getCommandDescriptions() {
			return "url\t\tu\tChange the full URL (Note: you have to include the '/announce' part).";
		}
	}	
}
