package threaded_crawler;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;

public class back_bone implements ActionListener{
	
	static String current;
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase("Select")){
			String selected=GUI.filelist.getSelectedValue().toString();
			StringTokenizer st = new StringTokenizer(selected, "|");
			if (!selected.equalsIgnoreCase("Back")){
				current=st.nextToken();
				try {
					make_list(current);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else{
				current=current.substring(0,current.lastIndexOf("/"));
				if (current!=GUI.root)
					try {
						make_list(current);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		}
		else if (e.getActionCommand().equalsIgnoreCase("Search")){
			String search = GUI.tf.getText().toLowerCase();
			if (GUI.ht.get(search)!=null){
				int count=1;
				GUI.jl.setText("<html>Path : <br>" + GUI.ht.get(search)+"</html>");
				String alternate_name = search+String.valueOf(count);
				while(GUI.ht.get(alternate_name)!=null){
					String text = GUI.jl.getText().replace("<html>", "").replace("</html>", "");
					GUI.jl.setText("<html>"+text+"<br>" + GUI.ht.get(alternate_name)+"</html>");
					alternate_name = search+String.valueOf(count);count++;
				}
			}
			else {
				GUI.jl.setText("<html>Message : File not Found but the following results are close : <br>" + GUI.ht.get(GUI.ht.ceilingKey(search))+"<br>"+GUI.ht.get(GUI.ht.higherKey(GUI.ht.ceilingKey(search)))+"<br>"+GUI.ht.get(GUI.ht.higherKey(GUI.ht.higherKey(GUI.ht.ceilingKey(search))))+"</html>");
				
			}
		}
	}
	void make_list(String selected) throws IOException{
		File file = new File(selected);
		if (!file.isDirectory()){
			GUI.jl.setText("Message : Selected File is not a Directory");
			Desktop desk = Desktop.getDesktop();
			desk.open(file);
			return;
		}
		File[] files = file.listFiles();
		DefaultListModel<String> filepaths = new DefaultListModel<String>();
		for (int i = 0;i<files.length;i++){
			if (!files[i].isHidden()){
				if (GUI.score.get(files[i].getPath())!=null)
					filepaths.addElement(files[i].getPath()+"|"+GUI.score.get(files[i].getPath()));
				else
					filepaths.addElement(files[i].getPath());
			}		
		}
		filepaths.addElement("Back");
		GUI.filelist.setModel(filepaths);
		GUI.filelist.setSelectedIndex(0);
	}
}
