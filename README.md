# void-extractor

To add this project as maven dependency simply follow instructions below:

1- Add the repository information below into repositories section of your pom.xml:

	<repositories>
	...
		<repository>
			<id>seagent.void.extractor</id>
			<url>https://raw.github.com/seagent/void-extractor/repository/</url>
			<releases>
				<enabled>true</enabled>
			</releases>
		</repository>
	...
	</repositories>
	
2- Add the dependency below into dependencies section of your pom.xml

	<dependencies>
	...
		<dependency>
			<groupId>Seagent</groupId>
			<artifactId>VOIDExtractor</artifactId>
			<version>0.0.1-2018.11.05</version>
		</dependency>
	...
	</dependencies>


All Done :)
