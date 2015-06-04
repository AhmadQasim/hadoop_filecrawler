package threaded_crawler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class GUI {
	static TreeMap<String, String> ht;
	static TreeMap<String, String>score;
	static Lock lt;static String root;
	static JFrame frame;static JButton button, button_1;static JList<String> filelist;static JPanel panel;static JTextField tf;static JLabel jl;
	
	@SuppressWarnings({ "resource" })
	public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException{
		lt = new ReentrantLock();
		Scanner in = new Scanner(System.in);
		root = in.next();
		if (!root.contains("/"))
			root=System.getProperty("user.dir")+"/"+root;
		ht = new TreeMap<String, String>();
		score = new TreeMap<String, String>();
		new File("mapred_in").mkdir();
		File file_in = new File(System.getProperty("user.dir")+"/mapred_in/dump.txt");
		if (file_in.exists()){file_in.delete();}
		FileWriter fw = new FileWriter(file_in);
		Thread th = new Thread(new crawler(root, fw));
		th.start();th.join();fw.close();
		Configuration conf = new Configuration();
		conf.set("mapred.jar", System.getProperty("user.dir")+"/"+"threaded_crawler.jar");
		Job job = Job.getInstance(conf, "file ranker");
		job.setMapperClass(TokenizerMapper.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path("mapred_in/dump.txt");
		fs.moveFromLocalFile(path, path);
		FileInputFormat.addInputPath(job, new Path("mapred_in"));
		FileOutputFormat.setOutputPath(job, new Path("mapred_out"));
		job.waitForCompletion(true);
		path = new Path("mapred_out");
		fs.moveToLocalFile(path, path);
		File file_out = new File(System.getProperty("user.dir")+"/mapred_out/part-r-00000");
		FileReader reader = new FileReader(file_out);
		BufferedReader bf = new BufferedReader(reader);
		populate_score(bf);
		GUI ui = new GUI();
		ui.setup(root);
	}
	public static void populate_score(BufferedReader bf) throws IOException{
		String buffer;
		while((buffer=bf.readLine()) != null){
			StringTokenizer st = new StringTokenizer(buffer);
			score.put(st.nextToken(), st.nextToken());
		}
	}
	public void setup(String path){
		frame = new JFrame("Desktop Crawler");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 800);
		panel = new JPanel();jl = new JLabel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		DefaultListModel<String> filepaths = new DefaultListModel<String>();
		filepaths.addElement(path);
		filepaths.addElement("Back");
		filelist = new JList<String>(filepaths);
		filelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    filelist.setSelectedIndex(0);filelist.setFixedCellWidth(1000);
	    filelist.setVisibleRowCount(3);filelist.setFixedCellHeight(20);
	    JScrollPane fileScroll = new JScrollPane(filelist);
	    panel.add(filelist);
	    panel.add(fileScroll);
		back_bone list = new back_bone();
		button = new JButton("Select");
		button.addActionListener(list);
		button_1 = new JButton("Search");
		button_1.addActionListener(list);
		tf = new JTextField();
		panel.add(button);
		panel.add(tf);
		panel.add(button_1);
		panel.add(jl);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true); 	
	}
}

