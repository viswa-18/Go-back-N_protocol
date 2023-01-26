//Sender code
//will act as a client
import javax.swing.*;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class sender implements ActionListener{
static JButton connect,sendNew,k_Packet,d_kPacket,reset,timerButton;
static JLabel[] packet;
static JList<String> jl;
static DefaultListModel<String> dlm;
static JScrollBar vertical;//to controll the vertical scrollBar of JScrollPane
static Timer doc_Tim;//doc_time to autoscroll to last added entry in JList(dlm)(we need to wait for 1 second,after doing dlm.addElement(),only then the scroll,could be adjusted,so we need timer)
static Timer timerr;
Socket S;//end point for communication
static DataInputStream ip;//to read data
static DataOutputStream op;//to write data
static JLabel timeDisplay;
LocalDateTime startTime;
Duration duration=Duration.ofSeconds(15); //duration of the timer
static int base=0;// next packet number to be sent
static int nextSeqNo=0; 
static JLabel seqNoLabel,baseLabel;
sender(){
//creating JFrame    
JFrame jf=new JFrame("Sender");
jf.setSize(1000,500);
jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
jf.setLayout(new BorderLayout());

//creating buttons
connect=new JButton("Create Connection");
connect.addActionListener(this);
connect.setBackground(new Color(0,168,106));
connect.setForeground(Color.black);
connect.setPreferredSize(new Dimension(25,25));
connect.setFont(new Font("SANS SERIF",Font.BOLD,15));
sendNew=new JButton("Send New Packet");
sendNew.addActionListener(this);
sendNew.setEnabled(false);
sendNew.setBackground(new Color(0,168,106));
sendNew.setForeground(Color.black);
sendNew.setPreferredSize(new Dimension(25,25));
sendNew.setFont(new Font("SANS SERIF",Font.BOLD,15));
k_Packet=new JButton("Kill Packet");
k_Packet.addActionListener(this);
k_Packet.setEnabled(false);
k_Packet.setBackground(new Color(0,168,106));
k_Packet.setForeground(Color.black);
k_Packet.setPreferredSize(new Dimension(25,25));
k_Packet.setFont(new Font("SANS SERIF",Font.BOLD,15));
d_kPacket=new JButton("Don't Kill Packet");
d_kPacket.addActionListener(this);
d_kPacket.setEnabled(false);
d_kPacket.setBackground(new Color(0,168,106));
d_kPacket.setForeground(Color.BLACK);
d_kPacket.setPreferredSize(new Dimension(25,25));
d_kPacket.setFont(new Font("SANS SERIF",Font.BOLD,15));
reset=new JButton("Reset");
reset.addActionListener(this);
reset.setEnabled(false);
reset.setBackground(new Color(0,168,106));
reset.setForeground(Color.BLACK);
reset.setPreferredSize(new Dimension(25,25));
reset.setFont(new Font("SANS SERIF",Font.BOLD,15));
timerButton=new JButton("Start Timer");
timerButton.setEnabled(false);
timerButton.setBackground(new Color(0,168,106));
timerButton.setForeground(Color.black);
connect.setPreferredSize(new Dimension(25,25));
connect.setFont(new Font("SANS SERIF",Font.BOLD,15));

//Creating pane for displaying time
timeDisplay=new JLabel("-- -- --");
timeDisplay.setFont(new Font("Sans Serif",Font.BOLD,15));
JLabel impPermanentInfo=new JLabel("Window Size=4");
impPermanentInfo.setFont(new Font("Sans Serif",Font.PLAIN,12));
impPermanentInfo.setOpaque(true);
impPermanentInfo.setBackground(Color.white);
JPanel timeDisplayPanel=new JPanel();
timeDisplayPanel.setLayout(new FlowLayout());
timeDisplayPanel.add(impPermanentInfo);
timeDisplayPanel.add(Box.createHorizontalStrut(120));
timeDisplayPanel.add(timeDisplay);
timeDisplayPanel.add(Box.createHorizontalStrut(10));
timeDisplayPanel.add(timerButton);
seqNoLabel=new JLabel("next Sequence no:"+nextSeqNo);
seqNoLabel.setFont(new Font("Sans Serif",Font.PLAIN,12));
baseLabel=new JLabel("base :"+base);
baseLabel.setFont(new Font("Sans Serif",Font.PLAIN,12));
timeDisplayPanel.add(Box.createHorizontalStrut(30));
timeDisplayPanel.add(seqNoLabel);
timeDisplayPanel.add(Box.createHorizontalStrut(10));
timeDisplayPanel.add(baseLabel);
baseLabel.setOpaque(true);
baseLabel.setBackground(Color.white);
seqNoLabel.setOpaque(true);
seqNoLabel.setBackground(Color.white);

//creating packets
packet=new JLabel[11];
for(int z=0;z<11;z++){
packet[z]=new JLabel(" "+z+" ",JLabel.CENTER);
packet[z].setPreferredSize(new Dimension(35,35));
packet[z].setFont(new Font("Trebuchet MS",Font.BOLD,18));
packet[z].setForeground(Color.black);
packet[z].setOpaque(true);
packet[z].setBackground(Color.white);
}
for(int z=0;z<4;z++){
packet[z].setForeground(Color.magenta);//magenta to indicate that the packets are in frame
packet[z].setPreferredSize(new Dimension(35,35));
packet[z].setFont(new Font("Trebuchet MS",Font.BOLD,18));
}

//creating pane to display summary
dlm=new DefaultListModel<String>();
jl=new JList<String>(dlm);
jl.setSize(500,500);
jl.setVisibleRowCount(30);
jl.setLayoutOrientation(JList.VERTICAL);
jl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
JScrollPane scrollArea=new JScrollPane(jl);
scrollArea.setSize(800, 500);
scrollArea.setBorder(BorderFactory.createEmptyBorder(0,0,10,20));
vertical=scrollArea.getVerticalScrollBar();
doc_Tim=new Timer(700,new ActionListener(){
public void actionPerformed(ActionEvent e1){
vertical.validate();//validation is updating "Maximum" of vertical
vertical.setValue( vertical.getMaximum() );
}
});
doc_Tim.setRepeats(false);
dlm.addListDataListener(new ListDataListener(){
public void contentsChanged(ListDataEvent e1){}
public void intervalRemoved(ListDataEvent e2) {}
public void intervalAdded(ListDataEvent e3) {
if(doc_Tim.isRunning()){
doc_Tim.restart();}
else{doc_Tim.start();}}
});

//creating pane to display buttons
JPanel buttonPane=new JPanel();
buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.X_AXIS));
buttonPane.add(connect);
buttonPane.add(Box.createHorizontalStrut(120));
buttonPane.add(sendNew);
buttonPane.add(Box.createHorizontalStrut(10));
buttonPane.add(k_Packet);
buttonPane.add(d_kPacket);
buttonPane.add(Box.createHorizontalStrut(120));
buttonPane.add(reset);
buttonPane.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

// creating pane to display packets
JPanel packetPane=new JPanel();
packetPane.setLayout(new FlowLayout());
packetPane.setBorder(BorderFactory.createEmptyBorder(80, 15, 80, 15));
for(int z=0;z<11;z++){
packetPane.add(packet[z]);
packetPane.add(Box.createHorizontalStrut(15));
}

//timer function
timerButton.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e1){
if(timerr.isRunning()){
timerr.stop();
startTime=null;
timerButton.setText("Start Timer");
}
else{
startTime=LocalDateTime.now();
timerr.start();
timerButton.setText("Stop Timer");
}
}
});
timerr=new Timer(300,new ActionListener(){
public void actionPerformed(ActionEvent e3){
LocalDateTime now=LocalDateTime.now();
Duration runningTime=Duration.between(startTime,now);
Duration timeLeft=duration.minus(runningTime);
if(timeLeft.isNegative() || timeLeft.isZero()){
timeLeft=Duration.ZERO;
timerButton.doClick();
goBackN();
}
timeDisplay.setText(String.format("00h 00m %02ds", timeLeft.getSeconds()));
}
});

//adding all panes,to main frame
jf.add(scrollArea,BorderLayout.LINE_END);
jf.add(buttonPane,BorderLayout.PAGE_END);
jf.add(packetPane,BorderLayout.CENTER);
jf.add(timeDisplayPanel,BorderLayout.PAGE_START);
jf.setVisible(true);
}//constructor end

//implementation of goback-N protocol
public static void goBackN(){
dlm.addElement("TIMEOUT for packet no: "+base);
dlm.addElement("Go-Back-N packets: "+base+"-"+(nextSeqNo-1));
sendNew.setVisible(false);
k_Packet.setEnabled(true);
d_kPacket.setEnabled(true);
k_Packet.setText("Kill Packets["+base+" -"+(nextSeqNo-1)+" ]");
d_kPacket.setText("don't Kill Packets["+base+" -"+(nextSeqNo-1)+" ]");
k_Packet.setVisible(false);
k_Packet.setVisible(true);
d_kPacket.setVisible(false);
d_kPacket.setVisible(true);
}
public static void implementingGoBackN(boolean pktsKilled){
timerButton.doClick();
dlm.addElement("Restarting Timer for packet no:"+base);
if(pktsKilled==false){
try{
for(int g=base;g<nextSeqNo;g++){
op.writeInt(2);
op.writeInt(g);
}
dlm.addElement("Packet no: "+base+"-"+(nextSeqNo-1)+" sent");
}catch(Exception e9){}
}
else{
dlm.addElement("Packet no: "+base+"-"+(nextSeqNo-1)+" got killed in network");
}
k_Packet.setVisible(false);
d_kPacket.setVisible(false);
k_Packet.setText("Kill Packet");
d_kPacket.setText("Don't Kill Packet");
d_kPacket.setVisible(true);
k_Packet.setVisible(true);
}
//when send new packet button is pressed
public static void sendNewPressed(boolean pktKilled){
if(nextSeqNo<base+4){
if(pktKilled==false){
try{
op.writeInt(2);
op.writeInt(nextSeqNo);
dlm.addElement("Packet no: "+(nextSeqNo)+" sent");}
catch(Exception e9){
dlm.addElement("Error while sending packet no"+(nextSeqNo)+" .");}
}
else{
dlm.addElement("Packet no"+(nextSeqNo)+" got killed in network");
}
if(base==nextSeqNo){
if(!timerButton.isEnabled()){
timerButton.setEnabled(true);}
timerButton.doClick();
dlm.addElement("Timer started for packet no: "+(nextSeqNo)+" .");/*start timer */
}
packet[nextSeqNo].setVisible(false);
packet[nextSeqNo].setBackground(Color.yellow);//yellow to indicate that packet is sent and waits for ACK
packet[nextSeqNo].setVisible(true);
nextSeqNo++;
seqNoLabel.setText("next Sequence no:"+nextSeqNo);
if(nextSeqNo==base+4){sendNew.setEnabled(false);}
}
else{dlm.addElement("Sending request REJECTED-exceeding window size(4)");
}
}

//When reset button is clicked
public void resetApplication(){
base=0;nextSeqNo=0;baseLabel.setText("base :"+base);
seqNoLabel.setText("next Sequence no:"+nextSeqNo);
dlm.clear();
if(timerr.isRunning()){
    timerButton.doClick();
}
timerButton.setEnabled(false);
dlm.addElement("TCP connection Successful..");
k_Packet.setVisible(false);
d_kPacket.setVisible(false);
k_Packet.setText("Kill Packet");
d_kPacket.setText("Don't Kill Packet");
k_Packet.setEnabled(false);
d_kPacket.setEnabled(false);
k_Packet.setVisible(true);
d_kPacket.setVisible(true);
timeDisplay.setText("-- -- --");
sendNew.setEnabled(true);sendNew.setVisible(true);
for(int z=0;z<11;z++){
packet[z].setVisible(false);
packet[z].setPreferredSize(new Dimension(35,35));
packet[z].setFont(new Font("Trebuchet MS",Font.BOLD,18));
packet[z].setForeground(Color.black);
packet[z].setBackground(Color.white);
packet[z].setVisible(true);}
for(int z=0;z<4;z++){
packet[z].setVisible(false);
packet[z].setForeground(Color.magenta);
packet[z].setPreferredSize(new Dimension(35,35));
packet[z].setFont(new Font("Trebuchet MS",Font.BOLD,18));
packet[z].setVisible(true);}
}
public void actionPerformed(ActionEvent e){
if(e.getSource()==connect){
if(e.getActionCommand().equals("Create Connection")){
try{ 
S=new Socket("localhost",500);//connecting to local host...creates socket and send request to connect to the server
ip=new DataInputStream(S.getInputStream());
op=new DataOutputStream(S.getOutputStream());
dlm.addElement("TCP Connection Successful...");
connect.setText("Close Connection");
sendNew.setEnabled(true);
reset.setEnabled(true);
}
catch(Exception ee){
dlm.addElement("TCP Connection Failed..");}
}
else{
try{op.writeInt(1);}
catch(Exception eee){
dlm.addElement("Error while closing connection");}
sendNew.setEnabled(false);
reset.setEnabled(false);
if(timerr.isRunning()){
timerButton.doClick();};
timerButton.setEnabled(false);
k_Packet.setEnabled(false);
d_kPacket.setEnabled(false);
dlm.addElement("Closing Socket...");
connect.setText("Rerun to connect");
connect.setEnabled(false);
}
}
else if(e.getSource()==sendNew){
sendNew.setVisible(false);
k_Packet.setEnabled(true);
d_kPacket.setEnabled(true);
}
else if(e.getSource()==k_Packet){
if(k_Packet.getText().equals("Kill Packet")){
sendNewPressed(true);}
else{
implementingGoBackN(true);}
sendNew.setVisible(true);
k_Packet.setEnabled(false);
d_kPacket.setEnabled(false);
}
else if(e.getSource()==d_kPacket){
if(d_kPacket.getText().equals("Don't Kill Packet")){
sendNewPressed(false);}
else{implementingGoBackN(false);}
sendNew.setVisible(true);
k_Packet.setEnabled(false);
d_kPacket.setEnabled(false);
}
else if(e.getSource()==reset){//when reset button is clicked
try{op.writeInt(3);}
catch(Exception e3){}
resetApplication();
}
}
public static void updateBase(int b_old,int b_new){
int y;
for( y=b_new;(y<b_new+4)&&(y<11);y++){
packet[y].setForeground(Color.magenta);
packet[y].setPreferredSize(new Dimension(35,35));
packet[y].setFont(new Font("Trebuchet MS",Font.BOLD,18));
}
for(y=b_old;y<b_new;y++){
packet[y].setVisible(false);
packet[y].setPreferredSize(new Dimension(35,35));
packet[y].setFont(new Font("Trebuchet MS",Font.BOLD,18));
packet[y].setBackground(Color.green);//green indicates that the packet is sent and ACK
packet[y].setForeground(Color.black);
packet[y].setVisible(true);
//setVisible() from false to true will repaint the component
}
if(!sendNew.isEnabled() &&(nextSeqNo-b_new<4)){
sendNew.setEnabled(true);
}// as only 4 packets are allowed to send without receiving ack,sendnew button is disabled
baseLabel.setText("base :"+b_new);
}
public static void main(String[] args)throws Exception {
//int p=ip.read();
new sender();
//receiving ack
int w=0;
while(true){
try{w=ip.readInt();
if(k_Packet.getText().equals("Kill Packet")){
// to ensure implementingGoBackN() is not in progress
dlm.addElement("ack: "+w+" received.");
updateBase(base,w+1);
base=w+1;
if(base==nextSeqNo){
timerButton.doClick();
timerButton.setEnabled(false);
dlm.addElement("Stoping Timer");/*stop timer */
}
else{
timerButton.doClick();
timerButton.doClick();
dlm.addElement("Restarting Timer");//Restarting timer
}
}
else{
dlm.addElement("ack: "+w+" discarded, as GBN procedure is in progress");
/*ACK received while implementing func: implementingGoBackN() will not be considered */}
}
catch(Exception e){
/*dlm.addElement("error:while receving ack");error coming,*/}
}
}//main end
}//class end