//Receiver code
//will act as a server(shd be executed before sender)
import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class receiver {
static JButton ack,d_ack;
static JLabel[] packet;
static DataInputStream ip;//to read data
static DataOutputStream op;//to write data
static JList<String> jl;
static DefaultListModel<String> dlm;
static JScrollBar vertical;//to controll the vertical scrollBar of JScrollPane
static Timer docTimer;//to autoscroll to last added entry in JList
static int expectedSeqNo=0;//next packet to be received
receiver(){
JFrame jf=new JFrame("Receiver");
jf.setSize(1000,500);
jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
jf.setLayout(new BorderLayout());

//creating buttons
ack=new JButton("Send ACK");
ack.setEnabled(false);
ack.setBackground(new Color(0,168,106));
ack.setForeground(Color.black);
ack.setPreferredSize(new Dimension(25,25));
ack.setFont(new Font("SANS SERIF",Font.BOLD,15));
ack.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e5){
try{op.writeInt(expectedSeqNo-1);
dlm.addElement("ack- "+(expectedSeqNo-1)+" sent");
}
catch(Exception ew){
dlm.addElement("error:sending ack- "+(expectedSeqNo-1));
}
ack.setEnabled(false);d_ack.setEnabled(false);
}
});
d_ack=new JButton("Kill ACK");
d_ack.setEnabled(false);
d_ack.setBackground(new Color(0,168,106));
d_ack.setForeground(Color.BLACK);
d_ack.setPreferredSize(new Dimension(25,25));
d_ack.setFont(new Font("SANS SERIF",Font.BOLD,15));
d_ack.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e9){
ack.setEnabled(false);
d_ack.setEnabled(false);
dlm.addElement("ack- "+(expectedSeqNo-1)+" killed");
}
});

//creating packets
packet=new JLabel[11];
for(int z=0;z<11;z++){
packet[z]=new JLabel(" "+z+" ",JLabel.CENTER);
packet[z].setPreferredSize(new Dimension(35,35));
packet[z].setFont(new Font("Trebuchet MS",Font.BOLD,18));
packet[z].setOpaque(true);
packet[z].setBackground(Color.WHITE);}
packet[0].setForeground(Color.magenta);//magenta indicates that the packet is in frame

//creating pane to display summary
dlm=new DefaultListModel<String>();
jl=new JList<String>(dlm);
jl.setLayoutOrientation(JList.VERTICAL);
jl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
jl.setVisibleRowCount(20);
JScrollPane scrollArea=new JScrollPane(jl);
scrollArea.setSize(700, 500);
scrollArea.setBorder(BorderFactory.createEmptyBorder(0,0,10,20));
vertical=scrollArea.getVerticalScrollBar();
docTimer=new Timer(700,new ActionListener(){
public void actionPerformed(ActionEvent e19){
vertical.validate();/*validation is updating "Maximum" of vertical*/
vertical.setValue( vertical.getMaximum() );}
});
docTimer.setRepeats(false);
dlm.addListDataListener(new ListDataListener(){
public void contentsChanged(ListDataEvent e1){}
public void intervalRemoved(ListDataEvent e2) {}
public void intervalAdded(ListDataEvent e3) {
if(docTimer.isRunning()){
docTimer.restart();}
else{docTimer.start();}}
});

//creating pane,to display buttons
JPanel buttonPane=new JPanel();
buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.X_AXIS));
buttonPane.add(ack);
buttonPane.add(Box.createHorizontalStrut(120));
buttonPane.add(d_ack);
buttonPane.setBorder(BorderFactory.createEmptyBorder(5,360,5,100));

// creating pane,to display packets
JPanel packetPane=new JPanel();
packetPane.setLayout(new FlowLayout());
packetPane.setBorder(BorderFactory.createEmptyBorder(80, 15, 80, 10));
for(int z=0;z<11;z++){packetPane.add(packet[z]);
packetPane.add(Box.createHorizontalStrut(5));}

// creating pane,for heading panel
JPanel headingPane=new JPanel();
JLabel heading=new JLabel("Window Size=1");
heading.setFont(new Font("Sans Serif",Font.PLAIN,12));
heading.setOpaque(true);
heading.setBackground(Color.white);
headingPane.setLayout(new FlowLayout());
headingPane.add(heading);

//adding all panes,to main frame
jf.add(scrollArea,BorderLayout.LINE_END);
jf.add(buttonPane,BorderLayout.PAGE_END);
jf.add(packetPane,BorderLayout.CENTER);
jf.add(headingPane,BorderLayout.PAGE_START);
jf.setVisible(true);
}

public static void afterReceivingPacket(){
int w;
try{w=ip.readInt();
if(w==expectedSeqNo){
dlm.addElement("packet no: "+w+" received");
expectedSeqNo++;
packet[expectedSeqNo-1].setForeground(Color.black);
packet[expectedSeqNo-1].setBackground(Color.CYAN);//CYAN indicates that packets are received and ACK sent
packet[expectedSeqNo-1].setPreferredSize(new Dimension(35,35));
packet[expectedSeqNo-1].setFont(new Font("Trebuchet MS",Font.BOLD,18));
if(expectedSeqNo<11){
packet[expectedSeqNo].setForeground(Color.magenta);
packet[expectedSeqNo].setPreferredSize(new Dimension(35,35));
packet[expectedSeqNo].setFont(new Font("Trebuchet MS",Font.BOLD,18));
}
}
else{dlm.addElement("packet no: "+w+" received,DISCARDED");}
ack.setEnabled(true);d_ack.setEnabled(true);
}catch(Exception ew){}
}

public static void resetApplication(){
dlm.clear();
dlm.addElement("Listening at port 500");
dlm.addElement("TCP connection established!!");
ack.setEnabled(false);
d_ack.setEnabled(false);
expectedSeqNo=0;
for(int z=0;z<11;z++){
packet[z].setVisible(false);
packet[z].setPreferredSize(new Dimension(35,35));
packet[z].setFont(new Font("Trebuchet MS",Font.BOLD,18));
packet[z].setBackground(Color.WHITE);
packet[z].setForeground(Color.black);
packet[z].setVisible(true);
}
packet[0].setVisible(false);
packet[0].setForeground(Color.magenta);
packet[0].setVisible(true);
packet[0].setPreferredSize(new Dimension(35,35));
packet[0].setFont(new Font("Trebuchet MS",Font.BOLD,18));
}
public static void main(String[] args) throws Exception{
new receiver();
ServerSocket SS=new ServerSocket(500);//to establish communication with the clients
//System.out.println("Listening at port 500");
Socket S=SS.accept();
dlm.addElement("Listening at port 500");
dlm.addElement("TCP connection established!!");
ip=new DataInputStream(S.getInputStream());//to read data
op=new DataOutputStream(S.getOutputStream());//to write data
int q;
while(true){
q=ip.readInt();
switch(q){
case 1:{S.close();SS.close();dlm.addElement("Closing Socket...");}
case 2:{afterReceivingPacket();break;}
case 3:{resetApplication();break;}
case 4:{;break;}
}
}
}//main end
}//class end