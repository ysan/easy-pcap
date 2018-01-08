CXX			:= /usr/bin/g++
MKDIR		:= /bin/mkdir
RM			:= /bin/rm

TARGET		:= testcap
SRCS		:= \
	main.cpp \
	Cap.cpp \
	ProxyThread.cpp \

OBJDIR		:= ./objs/
OBJS		:= $(SRCS:%.cpp=$(OBJDIR)/%.o)
DEPENDS		:= $(OBJS:%.o=%.d)

CFLAGS		:= -Wall -O0 -MD -std=c++11
INCLUDES	:= -I./

LIBS		:= -lpthread -lpcap
LDFLAGS		:= $(LIBS)


#.SUFFIXES: .c .cpp .o

.PHONY: all clean

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) -o $(TARGET) $^ $(LDFLAGS) $(CFLAGS)

$(OBJDIR)/%.o: %.cpp
	@$(MKDIR) -p $(dir $@)
	$(CXX) $(CFLAGS) $(INCLUDES) -c $< -o $@

clean:
	@$(RM) -f $(OBJS) $(TARGET) $(DEPENDS)

-include $(DEPENDS)
