package org.usfirst.frc.team1648.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	
	public static final double CONTROLLER_DEADZONE = 0.13; 
	
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	
	Victor climberMotor = new Victor(5);
	Victor rightDrive3 = new Victor(1);
	Victor rightDrive4 = new Victor(0);
	Victor gearMekMotor = new Victor(3);
	Victor leftDrive1 = new Victor(4);
	Victor leftDrive2 = new Victor (2);
	int climberCounter = 0;
	XBoxController controller = new XBoxController(0);
	
	@Override
	public void robotInit() {
		
	}
	
	@Override
	public void autonomousPeriodic() {
		
	}
	
	@Override
	public void teleopPeriodic() {
		arcadeDrive();
		if (controller.getAButton()){
			if (climberCounter < 400) {
				climberMotor.setSpeed(0.4);
				climberCounter++;
			}
		}
		System.out.println("A button: " + controller.getAButton());
		System.out.println("Climber counter: " + climberCounter);
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
		
		//Todo remove temp code
		leftMotorSpeed = leftMotorSpeed / 2;
		rightMotorSpeed = rightMotorSpeed / 2;
		
		leftMotorSpeed = -leftMotorSpeed; //left motor backwards
		
		leftDrive1.setSpeed(leftMotorSpeed);
		leftDrive2.setSpeed(leftMotorSpeed);
		rightDrive3.setSpeed(rightMotorSpeed);
		rightDrive4.setSpeed(rightMotorSpeed);		
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
		System.out.println("Right y = " + rightYAxis);
		System.out.println("Left y = " + leftYAxis);
		if (Math.abs(rightYAxis) < CONTROLLER_DEADZONE) {
			rightDrive3.setSpeed(0);
			rightDrive4.setSpeed(0);
		} else {
			rightDrive3.setSpeed(rightYAxis);
			rightDrive4.setSpeed(rightYAxis);
		}
		if (Math.abs(leftYAxis) < CONTROLLER_DEADZONE) {
			leftDrive1.setSpeed(0);
			leftDrive2.setSpeed(0);
		} else {
			leftDrive1.setSpeed(leftYAxis * -1.0);
			leftDrive2.setSpeed(leftYAxis * -1.0);
		}
	}
}