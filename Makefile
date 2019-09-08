### This is a makefile for compiling the Java project BoardGameEngine ###


# Dependencies
LIB_GT := game-template
LIB_GT_DIR := ../$(LIB_GT)

include $(LIB_GT_DIR)/common.mk


### Helper functions ###

# Run a command in the $(LIB_GT) directory
run_lib_gt = (cd $(LIB_GT_DIR) && $1)


### Configuration for sources file and compilation artefacts ###

# The name of the project
PROJECT_NAME = BoardGameEngine
# The name of the jar file to create
JAR_FILE := $(PROJECT_NAME).jar
# The name of the script which will run the jar
RUN_SCRIPT = $(PROJECT_NAME).$(SCRIPT_EXTENSION)
# The class containing `public static void main(String[] args)`
MAIN_CLASS := bge.main.BoardGameEngineMain

# Resource files such as fonts
RESOURCE_DIR := res
RESOURCE_FILES := $(call filter_findall,$(RESOURCE_DIR))

# Dependencies
LIB_GT_JAR := $(LIB_GT_DIR)/$(LIB_GT).jar
LIB_GT_SOURCE_DIR := ../$(LIB_GT)/$(SOURCE_DIR)
LIB_GT_CLASS_DIR := ../$(LIB_GT)/$(CLASS_DIR)
LIB_GT_SOURCE_FILES := $(call filter_find,$(LIB_GT_SOURCE_DIR),.java)

### Rules ###

# Define phony targets
.PHONY: all clean cleanall

### Default rule ###
.DEFAULT_GOAL := all
all: $(RUN_SCRIPT)

### Create game-template.jar ###
$(LIB_GT_JAR) : $(LIB_GT_SOURCE_FILES)
	$(call run_lib_gt,make)

### Compile `.java` files to `.class` files ###
# -cp - Specify classpath
# -d  - Set output directory
# -g  - Generate debugging information
JAVACFLAGS := \
	-cp "$(SOURCE_DIR)$(CP_SEPARATOR)$(LIB_GT_JAR)" \
	-d $(CLASS_DIR) \
	-g:none
# javac [ options ] [ sourcefiles ] [ classes ] [ @argfiles ]
$(CLASS_DIR)/%.class: $(SOURCE_DIR)/%.java | $(LIB_GT_JAR) $(CLASS_DIR)
	$(JAVAC) $(JAVACFLAGS) $<

### Copy `.class` files to the `.jar` archive ###
# c - Create a new archive
# u - Update an existing archive
# e - Set the application entry point
# f - Specify the file name
# v - Verbose output
JARFLAGS := cef
JARUPDATEFLAGS := uf
# jar c[efmMnv0] [entrypoint] [jarfile] [manifest] [-C dir] file ... [-Joption ...] [@arg-file ...]
# jar u[efmMnv0] [entrypoint] [jarfile] [manifest] [-C dir] file ... [-Joption ...] [@arg-file ...]
$(JAR_FILE): $(LIB_GT_JAR) $(CLASS_FILES) $(RESOURCE_FILES)
	$(JAR) $(JARFLAGS) $(MAIN_CLASS) $@ -C $(CLASS_DIR) . -C $(LIB_GT_CLASS_DIR) .
	$(JAR) $(JARUPDATEFLAGS) $@ -C $(RESOURCE_DIR) .

### Create a script to run the jar ###
# java [options] -jar filename [args]
$(RUN_SCRIPT): $(JAR_FILE)
	@echo $(JAVA) -jar $< > $@
	chmod 775 $@

### Remove the jar archive, the run script, and the class files ###
clean:
	$(call run_lib_gt,make clean)
	$(RM) $(JAR_FILE) $(RUN_SCRIPT) $(CLASS_FILES)
	
### Remove the jar archive, the run script, and the bin directory ###
cleanall:
	$(call run_lib_gt,make cleanall)
	$(RM) $(JAR_FILE) $(RUN_SCRIPT) $(CLASS_DIR)
