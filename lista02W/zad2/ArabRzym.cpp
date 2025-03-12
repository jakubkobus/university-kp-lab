#include "ArabRzym.hpp"

#define NUMS_SIZE 13

const std::string ArabRzym::liczbyRzymskie[] = {
  "I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"
};

const int ArabRzym::liczbyArabskie[] = {
  1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000
};

int ArabRzym::rzym2arab(const std::string &rzym)
{
  int result = 0;
  int prevValue = 4000;
  size_t i = 0;

  while(i < rzym.length()) {
    bool found = false;

    for(int j = NUMS_SIZE - 1; j >= 0; j--) {
      if(rzym.compare(i, liczbyRzymskie[j].length(), liczbyRzymskie[j]) == 0) {
        int value = liczbyArabskie[j];

        if(value > prevValue)
          throw ArabRzymException("nieprawidlowy zapis");
        
        result += value;
        prevValue = value;
        i += liczbyRzymskie[j].length();
        found = true;
        break;
      }
    }

    if(!found)
      throw ArabRzymException("nieprawidlowy zapis");
  }

  return result;
}

std::string ArabRzym::arab2rzym(int arab)
{
  if(arab < 1 || arab > 3999)
    throw ArabRzymException("liczba spoza zakresu (1-3999)");
  

  std::string result;
  for(int i = sizeof(liczbyArabskie) / sizeof(liczbyArabskie[0]) - 1; i >= 0; i--)
    while (arab >= liczbyArabskie[i]) {
      result += liczbyRzymskie[i];
      arab -= liczbyArabskie[i];
    }

  return result;
}