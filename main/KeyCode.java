package main;

/**
 * This is a reimplementation of JavaFX KeyCode Enum, without the use of JavaFX
 * I hope this doesn't violates any rule
 * @author rafael
 *
 */
public enum KeyCode {
	A(0x41),
	C(0x43),
	W(0x57),
	ALT(0x12),
	CONTROL(0x11),
	TAB(0x09);
	
	int keyCode;
	
	private KeyCode (int keycode) {
		this.keyCode = keycode;
	}
	
	public int getCode () {
		return this.keyCode;
	}
}
