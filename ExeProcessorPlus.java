import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;

enum ExeProcessorType {
	PRINT, EXPORT, OTHERS
}

public class ExeProcessorPlus {
	//Windows����cmd����ר��
	public static String CMD_INTRICATOR = "cmd /c ";
	//Windowsϵͳ���ζ�������˳��ִ�����ӷ�
	public static String EXE_INTRICATOR_SEPERATE = " & ";
	
	
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

	private void dealPipeData(String data, ExeProcessorType type, BufferedWriter writer) {
		try {
			switch (type) {
			case PRINT:
				System.out.println(data);
				break;
			case EXPORT:
				writer.write(data);
				writer.newLine();
				break;
			case OTHERS:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void internalExecute(ExeProcessorType type,String exportPath) {
		try {
			// �ڲ�ʹ�ö�������ɿ��ܵĶ���txt�ļ����
			File dstFile = null;
			FileWriter dstFileWriter = null;
			BufferedWriter dstbWriter = null;

			if (type == ExeProcessorType.EXPORT) {
				dstFile = new File(exportPath);
				dstFile.createNewFile();
				dstFileWriter = new FileWriter(dstFile);
				dstbWriter = new BufferedWriter(dstFileWriter);
			}

			// �ӹܵ��ж��������Ϣ
			if (dstbWriter != null) {
				dstbWriter.write("�����Ϣ");
				dstbWriter.newLine();
				dstbWriter.newLine();
			} else {
				System.out.println("�����Ϣ");
			}
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));

			for (String line; null != (line = stdOut.readLine());)
				dealPipeData(line, type, dstbWriter);

			// �ӹܵ��ж�������Ϣ
			if (dstbWriter != null) {
				dstbWriter.write("������Ϣ");
				dstbWriter.newLine();
				dstbWriter.newLine();
			} else {
				System.out.println("������Ϣ");
			}

			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			for (String line; null != (line = stdError.readLine());)
				dealPipeData(line, type, dstbWriter);

			if (dstbWriter != null) {
				dstbWriter.close();
			}

			if (dstFileWriter != null) {
				dstFileWriter.close();
			}

			if (stdOut != null) {
				stdOut.close();
			}

			if (stdError != null) {
				stdError.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public boolean executeRun(String path, Vector<String> argvs, ExeProcessorType type, String exportPath,
			boolean isParallel) {
		try {
			/*if (path.contains(" ")) {
				path = "\"" + path + "\"";
			}*/

			this.setExePath(path);
			this.setCommandStr(argvs);
			String cmdStr = exePath;
			for (int i = 0; i < argvs.size(); i++) {
				cmdStr = cmdStr + " " + argvs.get(i);
			}

			System.out.println(cmdStr);

			this.process = Runtime.getRuntime().exec(cmdStr);

			if (!isParallel) {
				this.process.waitFor();
			}

			// ��ܵ�����������
			/*
			 * new Thread() { public void run() { OutputStream stdin =
			 * process.getOutputStream(); for (int i = 0; stdin!=null ; i++) { try {
			 * Thread.sleep(1); // Ҫ��ϢƬ�̲ſ��õ� I/O �Ļ���Ч���� stdin.write((i + " " + i +
			 * "\n").getBytes()); } catch (Exception ex) { ex.printStackTrace(); } } }
			 * }.start();
			 */
			
			if (isParallel) {
				new Thread() {
					public void run() {
						internalExecute(type, exportPath);
					};
				}.start();
			}
			else {
				internalExecute(type, exportPath);
			}
			

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

		return true;
	}

	
	//�ú�����Windowsר�ã�Ϊ�˱�֤��Щexe�Ļ�������û�����õ�ȫ�ִӶ���Ҫ�������Ӧ�������в���ִ��
	public boolean executeRunForWindows(Vector<String> prefixGroups,
			String path, Vector<String> argvs, ExeProcessorType type, String exportPath,
			boolean isParallel) {
		try {
			
			this.setExePath(path);
			this.setCommandStr(argvs);
			String cmdStr = "";
			if (prefixGroups.size()!=0) {
				cmdStr=CMD_INTRICATOR;
				
				for (int i = 0; i < prefixGroups.size(); i++) {
					cmdStr=cmdStr+prefixGroups.get(i)+EXE_INTRICATOR_SEPERATE;
				}
			}
			
			cmdStr=cmdStr+exePath;
			
			for (int i = 0; i < argvs.size(); i++) {
				cmdStr = cmdStr + " " + argvs.get(i);
			}

			System.out.println(cmdStr);

			this.process = Runtime.getRuntime().exec(cmdStr);

			if (!isParallel) {
				this.process.waitFor();
			}

			// ��ܵ�����������
			/*
			 * new Thread() { public void run() { OutputStream stdin =
			 * process.getOutputStream(); for (int i = 0; stdin!=null ; i++) { try {
			 * Thread.sleep(1); // Ҫ��ϢƬ�̲ſ��õ� I/O �Ļ���Ч���� stdin.write((i + " " + i +
			 * "\n").getBytes()); } catch (Exception ex) { ex.printStackTrace(); } } }
			 * }.start();
			 */
			
			if (isParallel) {
				new Thread() {
					public void run() {
						internalExecute(type, exportPath);
					};
				}.start();
			}
			else {
				internalExecute(type, exportPath);
			}
			

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

		return true;
	}
	
	
	public static void main(String[] args) {
		// TODO �Զ����ɵķ������
		ExeProcessorPlus exeProcessorPlus = new ExeProcessorPlus();
		
		
		//��ʽһ:ֱ��ִ�ж�Ӧ��Exe
		String exePath="./exeDemo/test.exe";
		Vector<String> argvs = new Vector<String>();
		argvs.add("12131");
		argvs.add("456464564");

		exeProcessorPlus.executeRun(exePath, argvs, ExeProcessorType.PRINT, "", true);
		
		//��ʽ�����л�����Ӧ�Ļ�������ִ�ж�Ӧ��EXE(���Windowsϵͳ),Ϊ�˱�֤��Щ�ֲ������ܱ�������ʹ�ã�������õ�ǰ����f������exeDemo�е�test.exe��
		/*Vector<String> prefixGroups=new Vector<String>();
		prefixGroups.add("f:");
		prefixGroups.add("cd f:");
		String exePath="f:/test.exe";
		
		Vector<String> argvs = new Vector<String>();
		argvs.add("1545446");
		argvs.add("5959595");

		exeProcessorPlus.executeRunForWindows(prefixGroups, exePath, 
				argvs, ExeProcessorType.PRINT, "", true);*/
		
		while (true) {
			System.out.println("�������߳�");
		}
	}

}