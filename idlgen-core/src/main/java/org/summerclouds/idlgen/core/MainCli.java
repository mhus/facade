package org.summerclouds.idlgen.core;

import java.util.LinkedList;

import de.mhus.lib.core.MLog;

public class MainCli extends MLog implements Cli {

    public static void main(String[] args) throws Exception {

        if (args == null || args.length == 0) {
            System.out.println("Try -help");
            return;
        }
        
        LinkedList<String> queue = new LinkedList<String>();
        for (String arg : args) queue.add(arg);

        new MainCli().execute(queue);

    }

	private void execute(LinkedList<String> queue) {
		Model gen = new Model();
		
	}
    
}
