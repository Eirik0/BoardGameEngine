set workingdir=%cd%
if not exist %workingdir%\build (
	mkdir %workingdir%\build
)
if not exist BoardGameEngine.jar (
	xcopy /s %workingdir%\BoardGameEngine\res\* %workingdir%\build
	cd %workingdir%\BoardGameEngine\src
	for /r %%a in (.) do (
		cd %%a
		if exist *.java (
			javac -cp %workingdir%\BoardGameEngine\src -d %workingdir%\build *.java
		)
	)
	cd %workingdir%\build
	jar cvfe ..\BoardGameEngine.jar main.BoardGameEngineMain *
	cd %workingdir%
)
java -jar BoardGameEngine.jar
exit