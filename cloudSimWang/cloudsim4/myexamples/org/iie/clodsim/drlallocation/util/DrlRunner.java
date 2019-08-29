package org.iie.clodsim.drlallocation.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationAbstract;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationInterQuartileRange;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationLocalRegression;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationLocalRegressionRobust;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyMigrationStaticThreshold;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicy;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMaximumCorrelation;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMinimumMigrationTime;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyMinimumUtilization;
import org.cloudbus.cloudsim.power.PowerVmSelectionPolicyRandomSelection;
import org.iie.cloudsim.drlallocation.CrrvaVmAllocationPolicy;
import org.iie.cloudsim.drlallocation.DrlVmAllocationPolicy;
import org.iie.cloudsim.drlallocation.LeastVmAllocationPolicy;
import org.iie.cloudsim.drlallocation.MinPowerAllocationPolicy;
import org.iie.cloudsim.drlallocation.MostVmAllocationPolicy;
import org.iie.cloudsim.drlallocation.MyVmAllocationPolicyAbstract;
import org.iie.cloudsim.drlallocation.PssfVmAllocationPolicy;
import org.iie.cloudsim.drlallocation.RandomVmAllocationPolicy;

/**
 * The Class RunnerAbstract.
 * 
 * If you are using any algorithms, policies or workload included in the power
 * package, please cite the following paper:
 * 
 * Anton Beloglazov, and Rajkumar Buyya, "Optimal Online Deterministic
 * Algorithms and Adaptive Heuristics for Energy and Performance Efficient
 * Dynamic Consolidation of Virtual Machines in Cloud Data Centers", Concurrency
 * and Computation: Practice and Experience (CCPE), Volume 24, Issue 13, Pages:
 * 1397-1420, John Wiley & Sons, Ltd, New York, USA, 2012
 * 
 * @author Anton Beloglazov
 */
public class DrlRunner {

	/** The enable output. */
	private static boolean enableOutput;

	/** The broker. */
	protected static DatacenterBroker broker;

	/** The cloudlet list. */
	protected static List<Cloudlet> cloudletList;

	/** The vm list. */
	protected static List<Vm> vmList;

	/** The host list. */
	protected static List<PowerHost> hostList;
	

	/**
	 * The map where each key is a user id and each value are the vm ids belonging
	 * to the user.
	 */
	//private static Map<Integer, List<Integer>> userVmIdsMap = new HashMap<Integer, List<Integer>>();

	/**
	 * Run.
	 * 
	 * @param enableOutput
	 *            the enable output
	 * @param outputToFile
	 *            the output to file
	 * @param inputFolder
	 *            the input folder
	 * @param outputFolder
	 *            the output folder
	 * @param workload
	 *            the workload
	 * @param vmAllocationPolicy
	 *            the vm allocation policy
	 * @param vmSelectionPolicy
	 *            the vm selection policy
	 * @param parameter
	 *            the parameter
	 */
	public DrlRunner(boolean enableOutput, boolean outputToFile, String inputFolder, String outputFolder,
			String workload, String vmAllocationPolicy, String vmSelectionPolicy, String parameter) {

		try {
			initLogOutput(enableOutput, outputToFile, outputFolder, workload, vmAllocationPolicy, vmSelectionPolicy,
					parameter);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		init(inputFolder + "/" + workload);
		
		VmAllocationPolicy vmAllocationPolicyObject = getVmAllocationPolicy(vmAllocationPolicy, vmSelectionPolicy,
				parameter);

		start(getExperimentName(workload, vmAllocationPolicy, vmSelectionPolicy, parameter), outputFolder,
				vmAllocationPolicyObject, inputFolder + "/" + workload);
		
		

	}

	/**
	 * Inits the log output.
	 * 
	 * @param enableOutput
	 *            the enable output
	 * @param outputToFile
	 *            the output to file
	 * @param outputFolder
	 *            the output folder
	 * @param workload
	 *            the workload
	 * @param vmAllocationPolicy
	 *            the vm allocation policy
	 * @param vmSelectionPolicy
	 *            the vm selection policy
	 * @param parameter
	 *            the parameter
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws FileNotFoundException
	 *             the file not found exception
	 */
	protected void initLogOutput(boolean enableOutput, boolean outputToFile, String outputFolder, String workload,
			String vmAllocationPolicy, String vmSelectionPolicy, String parameter)
			throws IOException, FileNotFoundException {
		setEnableOutput(enableOutput);
		Log.setDisabled(!isEnableOutput());
		if (isEnableOutput() && outputToFile) {
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}

			File folder2 = new File(outputFolder + "/log");
			if (!folder2.exists()) {
				folder2.mkdir();
			}

			File file = new File(outputFolder + "/log/"
					+ getExperimentName(workload, vmAllocationPolicy, vmSelectionPolicy, parameter) + ".txt");
			file.createNewFile();
			Log.setOutput(new FileOutputStream(file));
		}
	}

	protected void init(String inputFolder) {

		try {
			CloudSim.init(1, Calendar.getInstance(), false);

//			broker = DrlHelper.createBroker("broker_0");
//			int brokerId = broker.getId();
//
//			vmList = DrlHelper.createVmList(brokerId,DrlConstants.NUMBER_OF_VMS);
			// cloudletList = DrlHelper.createCloudletListPlanetLab(inputFolder,brokerId,0);
//			cloudletList = DrlHelper.createCloudletListRandom(brokerId, DrlConstants.NUMBER_OF_VMS);
			hostList = DrlHelper.createHostList(DrlConstants.NUMBER_OF_HOSTS);
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
			System.exit(0);
		}
	}

	/**
	 * Starts the simulation.
	 * 
	 * @param experimentName
	 *            the experiment name
	 * @param outputFolder
	 *            the output folder
	 * @param vmAllocationPolicy
	 *            the vm allocation policy
	 */
	protected void start(String experimentName, String outputFolder, VmAllocationPolicy vmAllocationPolicy,
			String inputFolder) {
		
		
		System.out.println("Starting " + experimentName);

		try {
			DrlGlobalBroker drlGlobalBroker = new DrlGlobalBroker("GlobalBroker");

			PowerDatacenter datacenter = (PowerDatacenter) DrlHelper.createDatacenter("Datacenter",
					PowerDatacenter.class, hostList, vmAllocationPolicy);

			datacenter.setDisableMigrations(false);

//			broker.submitVmList(vmList);
//			broker.submitCloudletList(cloudletList);

			CloudSim.terminateSimulation(DrlConstants.SIMULATION_LIMIT);
			double lastClock = CloudSim.startSimulation();

			//List<Cloudlet> newList = broker.getCloudletReceivedList();
			//Log.printLine("Received " + newList.size() + " cloudlets");

			CloudSim.stopSimulation();

//			DrlHelper.printResults(datacenter, vmList, lastClock, experimentName, DrlConstants.OUTPUT_CSV,
//					outputFolder);

			if (vmAllocationPolicy instanceof MyVmAllocationPolicyAbstract) {
				
				DrlHelper.printMeticsResults(datacenter, (MyVmAllocationPolicyAbstract) vmAllocationPolicy, lastClock);
				((MyVmAllocationPolicyAbstract)vmAllocationPolicy).finalize();
			}
			
			if (vmAllocationPolicy instanceof DrlVmAllocationPolicy) {
				((DrlVmAllocationPolicy)vmAllocationPolicy).finalize();
			}
			
			

			

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
			System.exit(0);
		}

		Log.printLine("Finished " + experimentName);
	}

	/**
	 * Gets the experiment name.
	 * 
	 * @param args
	 *            the args
	 * @return the experiment name
	 */
	protected String getExperimentName(String... args) {
		StringBuilder experimentName = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			if (args[i].isEmpty()) {
				continue;
			}
			if (i != 0) {
				experimentName.append("_");
			}
			experimentName.append(args[i]);
		}
		return experimentName.toString();
	}

	/**
	 * Gets the vm allocation policy.
	 * 
	 * @param vmAllocationPolicyName
	 *            the vm allocation policy name
	 * @param vmSelectionPolicyName
	 *            the vm selection policy name
	 * @param parameterName
	 *            the parameter name
	 * @return the vm allocation policy
	 */
	protected VmAllocationPolicy getVmAllocationPolicy(String vmAllocationPolicyName, String vmSelectionPolicyName,
			String parameterName) {
		VmAllocationPolicy vmAllocationPolicy = null;
		PowerVmSelectionPolicy vmSelectionPolicy = null;
		if (!vmSelectionPolicyName.isEmpty()) {
			vmSelectionPolicy = getVmSelectionPolicy(vmSelectionPolicyName);
		}
		double parameter = 0;
		if (!parameterName.isEmpty()) {
			parameter = Double.valueOf(parameterName);
		}
		if (vmAllocationPolicyName.equals("iqr")) {
			PowerVmAllocationPolicyMigrationAbstract fallbackVmSelectionPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
					hostList, vmSelectionPolicy, 0.7);
			vmAllocationPolicy = new PowerVmAllocationPolicyMigrationInterQuartileRange(hostList, vmSelectionPolicy,
					parameter, fallbackVmSelectionPolicy);
		} else if (vmAllocationPolicyName.equals("mad")) {
			PowerVmAllocationPolicyMigrationAbstract fallbackVmSelectionPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
					hostList, vmSelectionPolicy, 0.7);
			vmAllocationPolicy = new PowerVmAllocationPolicyMigrationMedianAbsoluteDeviation(hostList,
					vmSelectionPolicy, parameter, fallbackVmSelectionPolicy);
		} else if (vmAllocationPolicyName.equals("lr")) {
			PowerVmAllocationPolicyMigrationAbstract fallbackVmSelectionPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
					hostList, vmSelectionPolicy, 0.7);
			vmAllocationPolicy = new PowerVmAllocationPolicyMigrationLocalRegression(hostList, vmSelectionPolicy,
					parameter, DrlConstants.SCHEDULING_INTERVAL, fallbackVmSelectionPolicy);
		} else if (vmAllocationPolicyName.equals("lrr")) {
			PowerVmAllocationPolicyMigrationAbstract fallbackVmSelectionPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
					hostList, vmSelectionPolicy, 0.7);
			vmAllocationPolicy = new PowerVmAllocationPolicyMigrationLocalRegressionRobust(hostList, vmSelectionPolicy,
					parameter, DrlConstants.SCHEDULING_INTERVAL, fallbackVmSelectionPolicy);
		} else if (vmAllocationPolicyName.equals("thr")) {

			vmAllocationPolicy = new MyVmAllocationPolicyAbstract(hostList, vmSelectionPolicy, parameter);
			// vmAllocationPolicy = new PowerVmAllocationPolicyMigrationStaticThreshold(
			// hostList,
			// vmSelectionPolicy,
			// parameter);
		} else if (vmAllocationPolicyName.equals("pssf")) {
			vmAllocationPolicy = new PssfVmAllocationPolicy(hostList, vmSelectionPolicy, parameter);

		} else if (vmAllocationPolicyName.equals("random")) {
			vmAllocationPolicy = new RandomVmAllocationPolicy(hostList, vmSelectionPolicy, parameter);
		} else if (vmAllocationPolicyName.equals("drl")) {
			vmAllocationPolicy = new DrlVmAllocationPolicy(hostList, vmSelectionPolicy, parameter);
		} else if (vmAllocationPolicyName.equals("least")) {
			vmAllocationPolicy = new LeastVmAllocationPolicy(hostList, vmSelectionPolicy, parameter);
		} else if (vmAllocationPolicyName.equals("most")) {
			vmAllocationPolicy = new MostVmAllocationPolicy(hostList, vmSelectionPolicy, parameter);
		}else if (vmAllocationPolicyName.equals("minP")) {
			vmAllocationPolicy = new MinPowerAllocationPolicy(hostList, vmSelectionPolicy, parameter);
		}else if (vmAllocationPolicyName.equals("crrva")) {
			vmAllocationPolicy = new CrrvaVmAllocationPolicy(hostList, vmSelectionPolicy, parameter);
		}else if (vmAllocationPolicyName.equals("dvfs")) {
			vmAllocationPolicy = new PowerVmAllocationPolicySimple(hostList);
		} else {
			System.out.println("Unknown VM allocation policy: " + vmAllocationPolicyName);
			System.exit(0);
		}
		return vmAllocationPolicy;
	}

	/**
	 * Gets the vm selection policy.
	 * 
	 * @param vmSelectionPolicyName
	 *            the vm selection policy name
	 * @return the vm selection policy
	 */
	protected PowerVmSelectionPolicy getVmSelectionPolicy(String vmSelectionPolicyName) {
		PowerVmSelectionPolicy vmSelectionPolicy = null;
		if (vmSelectionPolicyName.equals("mc")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyMaximumCorrelation(
					new PowerVmSelectionPolicyMinimumMigrationTime());
		} else if (vmSelectionPolicyName.equals("mmt")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyMinimumMigrationTime();
		} else if (vmSelectionPolicyName.equals("mu")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyMinimumUtilization();
		} else if (vmSelectionPolicyName.equals("rs")) {
			vmSelectionPolicy = new PowerVmSelectionPolicyRandomSelection();
		} else {
			System.out.println("Unknown VM selection policy: " + vmSelectionPolicyName);
			System.exit(0);
		}
		return vmSelectionPolicy;
	}

	/**
	 * Sets the enable output.
	 * 
	 * @param enableOutput
	 *            the new enable output
	 */
	public void setEnableOutput(boolean enableOutput) {
		DrlRunner.enableOutput = enableOutput;
	}

	/**
	 * Checks if is enable output.
	 * 
	 * @return true, if is enable output
	 */
	public boolean isEnableOutput() {
		return enableOutput;
	}


	public static class DrlGlobalBroker extends SimEntity {
		String[] vmArrNumStrArray = null;
		int vmArrNumStrArrayIndex = 0;

		private int brokerNameId = 0;

		private static final int CREATE_BROKER = 0;
		private List<Vm> vmList;
		private List<Cloudlet> cloudletList;
		private DatacenterBroker broker;
		private int totalCreatedVms = 0;
	//	private int totalCreatedCloudlets = 0;

		public DrlGlobalBroker(String name) {
			super(name);
			
			File file = new File(DrlConstants.VM_ARRIVE_NUM_FILE);
			BufferedReader reader = null;
			String vmArrNumString = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				
				vmArrNumString = reader.readLine();
				
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e1) {
					}
				}
			}

			vmArrNumStrArray = vmArrNumString.split(",");
		}

		@Override
		public void processEvent(SimEvent ev) {
			switch (ev.getTag()) {
			case CREATE_BROKER:
				
				int vmNum = Integer.parseInt(vmArrNumStrArray[vmArrNumStrArrayIndex++]);
				if((DrlConstants.NUMBER_OF_VMS - totalCreatedVms)<vmNum) {
					vmNum = DrlConstants.NUMBER_OF_VMS - totalCreatedVms;
				}
				
				CloudSim.pauseSimulation();
				
				broker = DrlHelper.createBroker("broker_" + (brokerNameId++));
				
				vmList = DrlHelper.createVmList(broker.getId(),vmNum, 
						totalCreatedVms);

				cloudletList = DrlHelper.createCloudletListRandom(broker.getId(),vmNum, totalCreatedVms);

				broker.submitVmList(vmList);
				broker.submitCloudletList(cloudletList);

				totalCreatedVms += vmNum;
				
				CloudSim.resumeSimulation();

				if (totalCreatedVms != DrlConstants.NUMBER_OF_VMS ) {
					schedule(getId(), DrlConstants.CREAT_VM_INTERVEL, CREATE_BROKER);
				}

				break;

			default:
				Log.printLine(getName() + ": unknown event type");
				break;
			}
		}

		@Override
		public void startEntity() {
			Log.printLine(super.getName() + " is starting...");
			schedule(getId(), DrlConstants.CREAT_VM_INTERVEL, CREATE_BROKER);
		}

		@Override
		public void shutdownEntity() {
		}

		public List<Vm> getVmList() {
			return vmList;
		}

		protected void setVmList(List<Vm> vmList) {
			this.vmList = vmList;
		}

		public List<Cloudlet> getCloudletList() {
			return cloudletList;
		}

		protected void setCloudletList(List<Cloudlet> cloudletList) {
			this.cloudletList = cloudletList;
		}

		// public DatacenterBroker getBroker() {
		// return broker;
		// }
		//
		// protected void setBroker(DatacenterBroker broker) {
		// this.broker = broker;
		// }
	}

}
