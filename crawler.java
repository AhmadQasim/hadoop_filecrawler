package threaded_crawler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

public class crawler implements Runnable{
	String path;
	FileWriter dump;
	public crawler(String current_path, FileWriter dump_file){
		this.path=current_path;
		this.dump = dump_file;
	}
	@Override
	public void run() {
		try {
		int thread_count=0;int max_threads=3;int count=0;String name, path_v;
		Thread[] th = new Thread[max_threads];
		File file = new File(path);
		if (!file.exists()){
			System.out.println("Path Not found.");
			return;
		}
		File[] files = file.listFiles();
		for (int i = 0;i<files.length;i++){
			if (!files[i].isDirectory()&&!files[i].isHidden()){
				GUI.lt.lock();name=files[i].getName().toLowerCase();path_v=files[i].getPath();
				Path path = files[i].toPath();
				BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
				int temp = (attr.isSymbolicLink()) ? 1 : 0;
				this.dump.append(files[i].getPath()+",");
				this.dump.append(attr.creationTime().to(TimeUnit.DAYS)+",");this.dump.append(temp+",");this.dump.append(attr.lastAccessTime().to(TimeUnit.DAYS)+",");this.dump.append(attr.lastModifiedTime().to(TimeUnit.DAYS)+",");this.dump.append(attr.size()+"\n");
				this.dump.flush();
				if (GUI.ht.get(name)==null)
					GUI.ht.put(name, path_v);
				else{
					String alternate_name = name;
					while(GUI.ht.get(alternate_name)!=null){
						alternate_name = name+String.valueOf(count);count++;
					}
					GUI.ht.put(alternate_name, path_v);
				}
				GUI.lt.unlock();
			}
			else if (files[i].isDirectory()&&!files[i].isHidden()){
				if (thread_count>=max_threads){
					for (int j=0;j<max_threads;j++){
							th[j].join();
					}
					thread_count=0;
				}
				th[thread_count] = new Thread(new crawler(files[i].getPath(), this.dump));th[thread_count].start();thread_count++;
			}			
		}
		for (int j=0;j<thread_count;j++){
				th[j].join();
		}
		} catch (InterruptedException | IOException e) {e.printStackTrace();}
	}
}

