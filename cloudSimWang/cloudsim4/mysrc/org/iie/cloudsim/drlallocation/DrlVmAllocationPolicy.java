package org.iie.cloudsim.drlallocation;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.iie.clodsim.drlallocation.util.DrlConstants;
import org.iie.cloudsim.util.CsvUtil;

public class DrlVmAllocationPolicy extends MyVmAllocationPolicyAbstract {
	int callFindTimes = 0;
	
	Socket socket = null;
	OutputStream os = null;
	PrintWriter pw = null;
	InputStream is = null;
	BufferedReader in = null;
	
	CsvUtil csvUtil = null;
	
	int preAction = -1;
	double preReward = 0;
	// List<Object> preState = null;
	// List<Object> curState = null;
	List<Integer> ob_t = null;
	List<Integer> ob_tp1 = null;
	List<Integer> ob_pad = null;
	
	boolean isForbidDec = false;
	int forbidAct = 0;

	public DrlVmAllocationPolicy(List<? extends Host> hostList, PowerVmSelectionPolicy vmSelectionPolicy,
			double utilizationThreshold) {
		super(hostList, vmSelectionPolicy, utilizationThreshold);
		// TODO Auto-generated constructor stub
		csvUtil = new CsvUtil(DrlConstants.DRL_EXPERIENCE_FILENAME);
		csvUtil.writeOneLine(generateCsvHead());
		
		ob_t = new ArrayList<Integer>();
		ob_tp1 = new ArrayList<Integer>();
		ob_pad = new ArrayList<Integer>();
		for (int j = 0; j < DrlConstants.NUMBER_OF_HOSTS; j++) {
			ob_t.add(0);// v num
			ob_t.add(0);// u num
			ob_t.add(0);// v belonging to new vm's user
			ob_t.add(0);//If the host has enough resources to attend the VM

		}
		ob_tp1.addAll(ob_t);
		ob_pad.addAll(ob_t);

		
		try {
			socket = new Socket("localhost", 8001);
			os = socket.getOutputStream();// 字节输出流
			pw = new PrintWriter(os);// 将输出流包装为打印流
			is = socket.getInputStream();
			in = new BufferedReader(new InputStreamReader(is));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		

		try {

			File file = new File(DrlConstants.DRL_DECISION_PATH);
			// 如果文件已存在，则先删除已存在的文件，再创建新的文件
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finalize() {
		try {
			// 向服务器端发送结束信息
			// System.out.println("bye");
			pw.write("bye;");
			pw.flush();

			socket.shutdownOutput();// 关闭输出流
			is.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	/**
	 * Finds a PM that has enough resources to host a given VM and that will not be
	 * overloaded after placing the VM on it. The selected host will be that one
	 * with most efficient power usage for the given VM.
	 * 
	 * @param vm
	 *            the VM
	 * @param excludedHosts
	 *            the excluded hosts
	 * @return the host found to host the VM
	 */
	@Override
	public PowerHost findHostForVm(Vm vm, Set<? extends Host> excludedHosts) {
		callFindTimes++;
		
//		if(callFindTimes < DrlConstants.NUMBER_OF_VMS/2) {
//			return super.findHostForVm(vm, excludedHosts);
//		}
		
		PowerHost allocatedHost = null;

		for(Host hostTmp:this.getHostList()) {
			int SameUserVmNum=0;
			for(Vm vmtmp2:hostTmp.getVmList()) {
				if(vmtmp2.getDrlUserId() == vm.getDrlUserId()) {
					SameUserVmNum++;
				}
			}
			
			ob_tp1.set(hostTmp.getId()* DrlConstants.OB_UNIT_LENGTH+2, SameUserVmNum);
			
			if(hostTmp.isSuitableForVm(vm)) {
				ob_tp1.set(hostTmp.getId() * DrlConstants.OB_UNIT_LENGTH + 3, 0 * DrlConstants.AMPLIFY_OB3);
			}else {
				ob_tp1.set(hostTmp.getId() * DrlConstants.OB_UNIT_LENGTH + 3, 1 * DrlConstants.AMPLIFY_OB3);
			}
		}
		
		StringBuffer buffer = new StringBuffer();
		String curStateStr = "";
		buffer.append("f");
		for (Object s : ob_tp1) {
			buffer.append(s).append(",");
		}
		buffer.append(";");
		curStateStr = buffer.toString();

		try {
			// Socket socket = new Socket("localhost", 8001);
			// Socket socket = new Socket();
			// socket.bind(new InetSocketAddress(9999));//绑定本地端口
			// socket.connect(new InetSocketAddress("localhost", 8001));//连接远程服务端接口

			pw.write(curStateStr);
			pw.flush();

			String info = in.readLine();
			int allocatedHostIndex = Integer.parseInt(info);
			// Log.printLine(String.format("Python服务器返回的主机id为："+allocatedHostIndex));
			// System.out.println("Python服务器返回的主机id为："+allocatedHostIndex);
			allocatedHost = getHostFromHostIndex(allocatedHostIndex);

			OutputStreamWriter osw = new OutputStreamWriter(
					new FileOutputStream(new File(DrlConstants.DRL_DECISION_PATH), true), "UTF-8");// 指定以UTF-8格式写入文件
			osw.write(String.format("%d,%d\n", callFindTimes, allocatedHostIndex));
			osw.close();
			
			if(ob_tp1.get(allocatedHostIndex * DrlConstants.OB_UNIT_LENGTH + 3) == 1) {
				List<Object> experienceForForbid = new ArrayList<Object>();
				
				experienceForForbid.add(allocatedHostIndex);
				experienceForForbid.add(-20);
				experienceForForbid.addAll(ob_tp1);
				experienceForForbid.addAll(ob_pad);
				
				// 向服务器端发送信息
				StringBuffer buffer2 = new StringBuffer();
				String recordsStr2 = "";
				buffer2.append("a");
				for (Object record2 : experienceForForbid) {
					buffer2.append(record2).append(",");
				}
				buffer2.append(";");
				recordsStr2 = buffer2.toString();
				pw.write(recordsStr2);
				pw.flush();
				
				csvUtil.writeOneLine(experienceForForbid);
				
//				pw.write("bye;");
//				pw.flush();
//				
//				socket.shutdownOutput();// 关闭输出流
//				is.close();
//				in.close();
//				socket.close();
//				
//				 //Log.printLine(String.format("The selected host is not available"));
//				System.out.println("The selected host is not available");
//				
//				System.exit(0);
				
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

        if (allocatedHost != null && !excludedHosts.contains(allocatedHost) && allocatedHost.isSuitableForVm(vm)) {
			
		} else {
			allocatedHost = super.findHostForVm(vm, excludedHosts);
		}

		int hostIndex = allocatedHost.getId();
		if (preAction == -1) {
			

			preAction = hostIndex;
			preReward = 0;
		} else {
			List<Object> experience = new ArrayList<Object>();
			

			experience.add(preAction);
			experience.add(preReward);
			experience.addAll(ob_t);

			for (Host hostTmp : this.getHostList()) {
				int sameUserVmNumTmp = 0;
				for (Vm vmtmp2 : hostTmp.getVmList()) {
					if (vmtmp2.getDrlUserId() == vm.getDrlUserId()) {
						sameUserVmNumTmp++;
					}
				}

				ob_tp1.set(hostTmp.getId() * DrlConstants.OB_UNIT_LENGTH + 2, sameUserVmNumTmp * DrlConstants.AMPLIFY_OB2);
				
				if(hostTmp.isSuitableForVm(vm)) {
					ob_tp1.set(hostTmp.getId() * DrlConstants.OB_UNIT_LENGTH + 3, 0 * DrlConstants.AMPLIFY_OB3);
				}else {
					ob_tp1.set(hostTmp.getId() * DrlConstants.OB_UNIT_LENGTH + 3, 1 * DrlConstants.AMPLIFY_OB3);
				}
			}

			experience.addAll(ob_tp1);
			csvUtil.writeOneLine(experience);

			
				// 向服务器端发送信息
				StringBuffer buffer2 = new StringBuffer();
				String recordsStr = "";
				buffer2.append("a");
				for (Object record : experience) {
					buffer2.append(record).append(",");
				}
				buffer2.append(";");
				recordsStr = buffer2.toString();
				pw.write(recordsStr);
				pw.flush();
			
			

			preAction = hostIndex;
			preReward=0;

			int sameUserVmNumInParH = ob_tp1.get(hostIndex * DrlConstants.OB_UNIT_LENGTH + 2) / DrlConstants.AMPLIFY_OB2;
			// 同驻度
			if (sameUserVmNumInParH == 0) {
				preReward = -2 * getHostUserNum(allocatedHost) * DrlConstants.REWARD_W1;
			} else {
				preReward = 0;
			}

			// LbCsp的reward
			double avgVmNum = ((double) this.getVmTable().size() + 1) / DrlConstants.NUMBER_OF_HOSTS;
			preReward -= (allocatedHost.getVmList().size() + 1 - avgVmNum) * (allocatedHost.getVmList().size() + 1 - avgVmNum) * DrlConstants.REWARD_W2;

			// LbU的reward
			preReward -= (sameUserVmNumInParH+1)* (sameUserVmNumInParH+1)  * DrlConstants.REWARD_W3;

			// 新开启一个主机
			if (allocatedHost.getVmList().size() == 0) {
				preReward -= 1 * DrlConstants.REWARD_W4;
			}
			preReward += DrlConstants.REWARD_BASE;
			
			ob_t.clear();
			ob_t.addAll(ob_tp1);

			
		}
		
		ob_tp1.set(hostIndex * DrlConstants.OB_UNIT_LENGTH, allocatedHost.getVmList().size() * DrlConstants.AMPLIFY_OB0);
		ob_tp1.set(hostIndex * DrlConstants.OB_UNIT_LENGTH + 1, getHostUserNum(allocatedHost) * DrlConstants.AMPLIFY_OB1);

				
		
		
		return allocatedHost;
	}
	
	private PowerHost getHostFromHostIndex(int hostIndex) {
		for (PowerHost host : this.<PowerHost>getHostList()) {
			if (host.getId() == hostIndex) {
				return host;
			}
		}
		return null;
	}
	
	protected List<Object> generateCsvHead() {
		List<Object> csvHead = new ArrayList<Object>();
		csvHead.add("action");
		csvHead.add("reward");

		for (int i = 0; i < 2; i++) {
			// csvHead.add("ob" + i + "_V_v");
			for (int j = 0; j < DrlConstants.NUMBER_OF_HOSTS; j++) {
				csvHead.add("ob" + i +"host" + j + "_1");
				csvHead.add("ob" + i +"host" + j + "_2");
				csvHead.add("ob" + i +"host" + j + "_3");
				csvHead.add("ob" + i +"host" + j + "_4");
			}
			
		}
		return csvHead;
	}
}
