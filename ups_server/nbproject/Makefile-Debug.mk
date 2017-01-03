#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-Linux
CND_DLIB_EXT=so
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/broadcaster.o \
	${OBJECTDIR}/checker.o \
	${OBJECTDIR}/cmd_arg.o \
	${OBJECTDIR}/com_stats.o \
	${OBJECTDIR}/console.o \
	${OBJECTDIR}/game.o \
	${OBJECTDIR}/game_list.o \
	${OBJECTDIR}/game_list_sender.o \
	${OBJECTDIR}/game_logic.o \
	${OBJECTDIR}/game_status_sender.o \
	${OBJECTDIR}/linked_list.o \
	${OBJECTDIR}/linked_list_iterator.o \
	${OBJECTDIR}/logger.o \
	${OBJECTDIR}/main.o \
	${OBJECTDIR}/message.o \
	${OBJECTDIR}/message_list.o \
	${OBJECTDIR}/player.o \
	${OBJECTDIR}/player_list.o \
	${OBJECTDIR}/player_list_sender.o \
	${OBJECTDIR}/printer.o \
	${OBJECTDIR}/request_parser.o \
	${OBJECTDIR}/string_builder.o \
	${OBJECTDIR}/string_utils.o \
	${OBJECTDIR}/tcp_communicator.o \
	${OBJECTDIR}/tcp_server.o \
	${OBJECTDIR}/tcp_server_control.o


# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-lpthread

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/ups_server

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/ups_server: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.c} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/ups_server ${OBJECTFILES} ${LDLIBSOPTIONS}

${OBJECTDIR}/broadcaster.o: broadcaster.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/broadcaster.o broadcaster.c

${OBJECTDIR}/checker.o: checker.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/checker.o checker.c

${OBJECTDIR}/cmd_arg.o: cmd_arg.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/cmd_arg.o cmd_arg.c

${OBJECTDIR}/com_stats.o: com_stats.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/com_stats.o com_stats.c

${OBJECTDIR}/console.o: console.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/console.o console.c

${OBJECTDIR}/game.o: game.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/game.o game.c

${OBJECTDIR}/game_list.o: game_list.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/game_list.o game_list.c

${OBJECTDIR}/game_list_sender.o: game_list_sender.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/game_list_sender.o game_list_sender.c

${OBJECTDIR}/game_logic.o: game_logic.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/game_logic.o game_logic.c

${OBJECTDIR}/game_status_sender.o: game_status_sender.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/game_status_sender.o game_status_sender.c

${OBJECTDIR}/linked_list.o: linked_list.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/linked_list.o linked_list.c

${OBJECTDIR}/linked_list_iterator.o: linked_list_iterator.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/linked_list_iterator.o linked_list_iterator.c

${OBJECTDIR}/logger.o: logger.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/logger.o logger.c

${OBJECTDIR}/main.o: main.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/main.o main.c

${OBJECTDIR}/message.o: message.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/message.o message.c

${OBJECTDIR}/message_list.o: message_list.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/message_list.o message_list.c

${OBJECTDIR}/player.o: player.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/player.o player.c

${OBJECTDIR}/player_list.o: player_list.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/player_list.o player_list.c

${OBJECTDIR}/player_list_sender.o: player_list_sender.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/player_list_sender.o player_list_sender.c

${OBJECTDIR}/printer.o: printer.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/printer.o printer.c

${OBJECTDIR}/request_parser.o: request_parser.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/request_parser.o request_parser.c

${OBJECTDIR}/string_builder.o: string_builder.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/string_builder.o string_builder.c

${OBJECTDIR}/string_utils.o: string_utils.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/string_utils.o string_utils.c

${OBJECTDIR}/tcp_communicator.o: tcp_communicator.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/tcp_communicator.o tcp_communicator.c

${OBJECTDIR}/tcp_server.o: tcp_server.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/tcp_server.o tcp_server.c

${OBJECTDIR}/tcp_server_control.o: tcp_server_control.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.c) -g -std=c11 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/tcp_server_control.o tcp_server_control.c

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/ups_server

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
