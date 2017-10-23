package org.usfirst.frc.team1648.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	
	public static final double CONTROLLER_DEADZONE = 0.13; 
	public double autonSpeed = 0.0;
	public static final double AUTON_SPEED_GROWTH = 0.02;
	public static final double AUTON_MAX_SPEED = 0.4;
	public static final int AUTON_MAX_LOOPS = 120;
	public static final int GATEDROP_LOOPS = 20;
	public static final double CLIMBER_MOTOR_SPEED = 0.4;
	public static final double GEARMEK_MOTOR_SPEED = 0.4;
	
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	boolean arcadeDrive = true;
	int sleepCounter = 0;
	int autonCounter = 0;
	int climberCounter = 0;
	int gateDropCounter = 0;
	
	Victor rightDrive1 = new Victor(1); 
	Victor rightDrive2 = new Victor (0);
	Victor leftDrive3 = new Victor(5); 
	Victor leftDrive4 = new Victor(4);
	Victor climberMotor1 = new Victor(3); //2 originally
	Victor climberMotor2 = new Victor (6);
	Victor gearMekMotor = new Victor(2); // labeled Victor1
	
	XBoxController controller = new XBoxController(0);
	PowerDistributionPanel pdp = new PowerDistributionPanel(0);
	
	@Override
	public void robotInit() {
	
	}
	
	@Override
	public void autonomousPeriodic() {
		if (autonSpeed < AUTON_MAX_SPEED && autonCounter < AUTON_MAX_LOOPS) {
			autonSpeed+= AUTON_SPEED_GROWTH;
		}
		
		rightDrive1.setSpeed(autonSpeed);
		rightDrive2.setSpeed(autonSpeed);
		leftDrive3.setSpeed(autonSpeed);
		leftDrive4.setSpeed(autonSpeed);
		
		if (autonCounter > AUTON_MAX_LOOPS) {
			rightDrive1.setSpeed(0);
			rightDrive2.setSpeed(0);
			leftDrive3.setSpeed(0);
			leftDrive4.setSpeed(0);
		}
		
		if (gateDropCounter < GATEDROP_LOOPS) {
			gearMekMotor.setSpeed(GEARMEK_MOTOR_SPEED);
			gateDropCounter++;
		} else {
			gearMekMotor.setSpeed(0.0);
		}
		
		autonCounter++;
		
		//Inverting motors
		leftDrive3.setSpeed(leftDrive3.getSpeed() * -1);
		leftDrive4.setSpeed(leftDrive4.getSpeed() * -1);
	}
	
	@Override
	public void teleopPeriodic() {
		leftDrive3.setInverted(true);
		leftDrive4.setInverted(true);
		
		if (arcadeDrive) {
			arcadeDrive();
		} else {
			tankDrive();
		}
		
		//Arcade/tank switch
		if (sleepCounter > 0) {
			sleepCounter--;
		} else if (controller.getYButton() && controller.getBButton()) {
			arcadeDrive = !arcadeDrive;
			//sleepCounter prevents rapid toggling of drive state
			sleepCounter = 100;
		}
		
		if (controller.getAButton()){
			climberMotor1.setSpeed(CLIMBER_MOTOR_SPEED);
			climberMotor2.setSpeed(CLIMBER_MOTOR_SPEED);
		} else {
			climberMotor1.setSpeed(0);
			climberMotor2.setSpeed(0);
		}
		
		//Print out any motors that are drawing current
		for (int c = 0; c < 16; c++) {
			if (pdp.getCurrent(c) > 0) {
				//System.out.println("Channel " + c + " = " + pdp.getCurrent(c));
			}
		}
		
		//Inverting motors
		leftDrive3.setSpeed(leftDrive3.getSpeed() * -1);
		leftDrive4.setSpeed(leftDrive4.getSpeed() * -1);
		climberMotor2.setSpeed(climberMotor2.getSpeed() * -1);
	}
	
	public void arcadeDrive() {
		double leftYAxis = controller.getLeftYAxis();
		double rightXAxis = controller.getRightXAxis();
		
		if (Math.abs(leftYAxis) < CONTROLLER_DEADZONE) {
			leftYAxis = 0;
		}
		
		if (Math.abs(rightXAxis) < CONTROLLER_DEADZONE) {
			rightXAxis = 0;
		}
		
		double leftMotorSpeed = leftYAxis;
		double rightMotorSpeed = leftYAxis;
		
		if (leftYAxis >= 0) {
			leftMotorSpeed -= rightXAxis;
			rightMotorSpeed += rightXAxis;
		} else {
			leftMotorSpeed += rightXAxis;
			rightMotorSpeed -= rightXAxis;
		}
		
		leftMotorSpeed = capAtOne(leftMotorSpeed);
		rightMotorSpeed = capAtOne(rightMotorSpeed);
				
		rightDrive1.setSpeed(rightMotorSpeed);
		rightDrive2.setSpeed(rightMotorSpeed);
		leftDrive3.setSpeed(leftMotorSpeed);
		leftDrive4.setSpeed(leftMotorSpeed);
	}
	
	public static double capAtOne(double x) {
		if (x > 1.0) {
			return 1.0;
		} else if (x < -1.0) {
			return -1.0;
		}
		return x;
	}
	
	public void tankDrive() {	
		double rightYAxis = controller.getRightYAxis(); 
		double leftYAxis = controller.getLeftYAxis(); 
		if (Math.abs(leftYAxis) < CONTROLLER_DEADZONE) {
			leftDrive3.setSpeed(0);
			leftDrive4.setSpeed(0);
		} else {
			leftDrive3.setSpeed(leftYAxis);
			leftDrive4.setSpeed(leftYAxis);
		}
		if (Math.abs(rightYAxis) < CONTROLLER_DEADZONE) {
			rightDrive1.setSpeed(0);
			rightDrive2.setSpeed(0);
		} else {
			rightDrive1.setSpeed(rightYAxis);
			rightDrive2.setSpeed(rightYAxis);
		}
	}
}