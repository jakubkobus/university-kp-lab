import java.util.*;

public class Test {
  public static void main(String[] args) {
    if(args.length < 1) {
      System.out.println("Brak argumentow");
      return;
    }

    int n;
    WierszTrojkataPascala wiersz;

    try {
      n = Integer.parseInt(args[0]);

      if(n < 0)
        throw new NumberFormatException();

      wiersz = new WierszTrojkataPascala(n);
      wiersz.wygenerujWiersz();
    } catch(NumberFormatException e) {
      System.out.println(args[0] + " - Nieprawidlowy numer wiersza");
      return;
    }

    for(int i = 1; i < args.length; i++) {
      try {
        int m = Integer.parseInt(args[i]);

        if(m < 0 || m > n)
          throw new IllegalArgumentException();

        System.out.println(args[i] + " - " + wiersz.m_tyElementWiersza(m));
      } catch(NumberFormatException e) {
        System.out.println(args[i] + " - " + "nieprawidlowa dana");
      } catch(IllegalArgumentException e) {
        System.out.println(args[i] + " - " + "liczba spoza zakresu");
      }
    }
  }
}

