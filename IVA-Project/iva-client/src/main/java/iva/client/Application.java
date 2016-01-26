/**
 * 
 */
package iva.client;

import iva.client.swing.ApplicationWindow;

import java.awt.EventQueue;

/**
 * Entry point of the IVA Client application.
 * 
 * @author Aron
 */
public class Application {

	public static void main(String[] args) {
		EventQueue.invokeLater(new ApplicationWindow());
	}
}
