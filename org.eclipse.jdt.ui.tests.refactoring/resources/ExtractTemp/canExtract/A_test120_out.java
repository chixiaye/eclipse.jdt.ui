package p; //7, 63, 7, 87

class A {
	public void m(String s) {
		if (s != null && s.length() >= 2) {
			char charAt= s.charAt(s.length() - 1);
			if (s.length() % 2 == 0)
				System.out.println(s.charAt(s.length() - 2) + charAt);
			if (s.length() % 2 == 1)
				System.out.println(charAt + s.charAt(s.length() - 2));
		} else if (s != null && s.length() == 1) {
			char charAt_1= s.charAt(s.length() - 1);
			System.out.println(charAt_1);
		}
		return;
	}
}
