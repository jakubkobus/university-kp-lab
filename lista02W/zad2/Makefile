CXX = g++
CXXFLAGS = -Wall -pedantic
TARGET = main
SOURCES = main.cpp ArabRzym.cpp
OBJECTS = $(SOURCES:.cpp=.o)

compile: $(TARGET)

$(TARGET): $(OBJECTS)
	$(CXX) $(CXXFLAGS) -o $@ $^

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $<

clean:
	rm -f $(OBJECTS) $(TARGET)
