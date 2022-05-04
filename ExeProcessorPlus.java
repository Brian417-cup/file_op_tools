import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;

public class ExeProcessorPlus{
	private String exePath;
	private Process process = null;
	private Vector<String> argvs;
	
	public String getExePath() {
		return exePath;
	}

	public void setExePath(String exePath) {
		this.exePath = exePath;
	}

	public Vector<String> getCommandStr() {
		return argvs;
	}

	public void setCommandStr(Vector<String> commandStr) {
		this.argvs = commandStr;
	}
	
	public boolean executeRun(String path, Vector<String> argvs,boolean isParallel) {
		try {
			if (path.contains(" ")) {
				path="\""+path+"\"";
			}
			
			this.setExePath(path);
			this.setCommandStr(argvs);
			String cmdStr=exePath;
			for (int i = 0; i < argvs.size(); i++) {
				cmdStr=cmdStr+" "+argvs.get(i);
			}
			
			System.out.println(cmdStr);
			
			this.process = Runtime.getRuntime().exec(cmdStr);
			
			if (!isParallel) {
				this.process.waitFor();	
			}
			
			
			 // ��ܵ�����������
	        /*new Thread() {
	            public void run() {
	                OutputStream stdin = process.getOutputStream();
	                for (int i = 0; stdin!=null ; i++) {
	                    try {
	                        Thread.sleep(1);   // Ҫ��ϢƬ�̲ſ��õ� I/O �Ļ���Ч����
	                        stdin.write((i + " " + i + "\n").getBytes());
	                    } catch (Exception ex) {
	                        ex.printStackTrace();
	                    }
	                }
	            }
	        }.start();*/
			
	        // �ӹܵ��ж��������Ϣ
			new Thread() {
				public void run() {
					BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			        try {
						for (String line; null != (line = stdOut.readLine()); )
						    System.out.println(line);
					} catch (IOException e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}		
				};
			}.start();
	        
	    
			//�ӹܵ��ж�������Ϣ
			new Thread() {
				public void run() {
					BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			        try {
						for (String line; null != (line = stdError.readLine()); )
						    System.out.println(line);
					} catch (IOException e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}		
				};
			}.start();
			
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
		return true;
	}
	

	
	public static void main(String[] args) {
		// TODO �Զ����ɵķ������
		ExeProcessorPlus exeProcessorPlus=new ExeProcessorPlus();
		Vector<String> argvs=new Vector<String>();
		argvs.add("123");
		argvs.add("456");
		
		
		exeProcessorPlus.executeRun("./exeDemo/test.exe", argvs,true);
		
		while (true) {
			System.out.println("�������߳�");
		}
	}

}
