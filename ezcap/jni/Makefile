CXX			:= /usr/bin/g++
MKDIR		:= /bin/mkdir
RM			:= /bin/rm

TARGET		:= libcapjni.so
SRCS		:= \
	com_nativewrapper_cap_CapJni.cpp \
	CapJniListener.cpp \
	cap/Cap.cpp \
	cap/ProxyThread.cpp \

OBJDIR		:= ./objs/
OBJS		:= $(SRCS:%.cpp=$(OBJDIR)/%.o)
DEPENDS		:= $(OBJS:%.o=%.d)

CFLAGS		:= -Wall -O0 -MD -std=c++11
INCLUDES	:= \
	-I./ \
	-I./cap \
	-I/usr/lib/jvm/java-1.8.0/include \
	-I/usr/lib/jvm/java-1.8.0/include/linux \

LIBS		:= -lpthread -lpcap
LDFLAGS		:= -fPIC -shared $(LIBS)


#.SUFFIXES: .c .cpp .o

.PHONY: all clean

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CXX) -o $(TARGET) $^ $(LDFLAGS) $(CFLAGS)

$(OBJDIR)/%.o: %.cpp
	@$(MKDIR) -p $(dir $@)
	$(CXX) $(LDFLAGS) $(CFLAGS) $(INCLUDES) -c $< -o $@

clean:
	@$(RM) -f $(OBJS) $(TARGET) $(DEPENDS)

-include $(DEPENDS)
