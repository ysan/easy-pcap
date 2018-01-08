CXX			:= /usr/bin/g++
MKDIR		:= /bin/mkdir
RM			:= /bin/rm

TARGET		:= libcapjni.so
SRCS		:= \
	com_jnistudy_cap_CapJni.cpp \
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
	-I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.0.fc20.x86_64/include \
	-I/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.79-2.5.5.0.fc20.x86_64/include/linux \
	-I/usr/lib/jvm/java-1.7.0-openjdk-amd64/include \
	-I/usr/lib/jvm/java-1.7.0-openjdk-amd64/include/linux \

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
