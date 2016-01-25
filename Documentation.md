This page gives a general view of our project.

# Introduction #
Ever imagined to play a piano tune on a computer? Or recording your own voices and play a melody with it? This is what our group project all about – a musical instrument simulator.

With Advanced Visual Instrument, one can save all the troubles to buy and keep the instruments. One can even compose songs in anywhere and anytime!

The idea of this project comes from a Korean variety show. A group of musicians has composed a song using an artist’s voice that their piano has recorded on the spot. This has inspired us and we have decided to come out with this project to share the joy of music.


---


# Team Members #
Khoo Ka Thoong

Wong Seok Yee

Lim Yeong Chuin

Choo Seah Ling


---


# Usage Scenarios #
1.      Joe is a pianist. He got an idea of a new song and wishes to compose it on the spot but he doesn't have a piano with him. He had his laptop with him and using Advanced Visual Instrument, he started to compose his song using the mock-up piano in the program.

2.      John wishes to use his own voice as the instrument. He recorded his voice using “Record from Microphone” functions and plays it like any other instrument.

3.      Bobby has recorded his voice in Advanced Visual Instrument before. He wishes to use the voice recorded back to compose a new song.



---


# Use Case Diagram #
Project overview.
![http://lh3.ggpht.com/_jnNNAl2ZuXg/TML7qqUWkMI/AAAAAAAAABo/JUrZ6pw62jY/s800/userCase.jpg](http://lh3.ggpht.com/_jnNNAl2ZuXg/TML7qqUWkMI/AAAAAAAAABo/JUrZ6pw62jY/s800/userCase.jpg)


---


# Sequence Diagram #

### Use Case 1 ###
![http://lh5.ggpht.com/_jnNNAl2ZuXg/TMMUtqDXO1I/AAAAAAAAABw/8I918LW3XYI/s800/1.jpeg](http://lh5.ggpht.com/_jnNNAl2ZuXg/TMMUtqDXO1I/AAAAAAAAABw/8I918LW3XYI/s800/1.jpeg)

### Use Case 2 ###
![http://lh3.ggpht.com/_jnNNAl2ZuXg/TMMUt8wASVI/AAAAAAAAAB0/TfBN9V_GNFw/s800/2.jpeg](http://lh3.ggpht.com/_jnNNAl2ZuXg/TMMUt8wASVI/AAAAAAAAAB0/TfBN9V_GNFw/s800/2.jpeg)

### Use Case 3 ###
![http://lh3.ggpht.com/_jnNNAl2ZuXg/TMMUt46MVEI/AAAAAAAAAB4/1jFM5ZiXDqA/s800/3.jpeg](http://lh3.ggpht.com/_jnNNAl2ZuXg/TMMUt46MVEI/AAAAAAAAAB4/1jFM5ZiXDqA/s800/3.jpeg)



---

# UML Class Diagram #
http://lh6.ggpht.com/_jnNNAl2ZuXg/TMTajcS8i1I/AAAAAAAAACM/bRTJQ_XJnFo/s800/App_Frame.PNG

http://lh3.ggpht.com/_jnNNAl2ZuXg/TMTajZ89dSI/AAAAAAAAACQ/c5USmmKcgqY/s800/AudioCapture.PNG

http://lh3.ggpht.com/_jnNNAl2ZuXg/TMTajkisKqI/AAAAAAAAACU/Dw1h9Ie6ghc/s800/Piano.PNG

http://lh6.ggpht.com/_jnNNAl2ZuXg/TMTaj-8WATI/AAAAAAAAACY/xiJyyZVBDE4/s800/PitchPiano.PNG

http://lh6.ggpht.com/_jnNNAl2ZuXg/TMTakasQpPI/AAAAAAAAACc/i_XtiG2T5h8/s800/SongPanel.PNG

http://lh3.ggpht.com/_jnNNAl2ZuXg/TMTav4tLjiI/AAAAAAAAACg/nl3r43WPgcw/s800/SongTable.PNG


---

# Proposed User Interfaces #
### Sketches ###

**Ver. 1** - Done on 29th July 2010
![http://lh6.ggpht.com/_BT5G2024qqo/TI3GE7Eh3JI/AAAAAAAAABc/zmW1PUynhuM/s700/GUI%20ver%201.jpeg](http://lh6.ggpht.com/_BT5G2024qqo/TI3GE7Eh3JI/AAAAAAAAABc/zmW1PUynhuM/s700/GUI%20ver%201.jpeg)

**Ver. 2** - Done on 12th August 2010
<table><tr><td><a <img src='http://lh5.ggpht.com/_BT5G2024qqo/TI3GE5_stoI/AAAAAAAAABg/2I3KXKSlcpc/s700/GUI%20ver%202.jpeg' />

<hr />
<h3>Ideas from members</h3>

<b>Choo Seah Ling</b>

<i>Main frame</i>

<a href='http://lh3.ggpht.com/_BT5G2024qqo/TJYzeEwYiPI/AAAAAAAAACA/CEoQlcoFcRs/s600/GUI_1_csl.PNG'>http://lh3.ggpht.com/_BT5G2024qqo/TJYzeEwYiPI/AAAAAAAAACA/CEoQlcoFcRs/s600/GUI_1_csl.PNG</a>

Description :<br>
When user opens an existing music file, a new panel will be added to the main frame from top to bottom. As shown in the figure, a panel for file named "File1" is added. User can implement all the program features easily. The buttons (Record, Stop, Play All) on the bottom of the main frame link to the Playback and Mixing module. User can add new music file by choosing File option in menu bar.<br>
<br>
<br>
<i>Virtual Piano</i>

<a href='http://lh6.ggpht.com/_BT5G2024qqo/TJYzeGiTPAI/AAAAAAAAACE/auOrUTU2rWo/s600/GUI_2_csl.PNG'>http://lh6.ggpht.com/_BT5G2024qqo/TJYzeGiTPAI/AAAAAAAAACE/auOrUTU2rWo/s600/GUI_2_csl.PNG</a>

Description :<br>
An internal frame that will pop up when user choose to add new file or compose new song. User can choose instruments available from a drop-down list. Playing music often involve  more than one key at one time. Therefore, this design let the user switch between using mouse and keyboard. This internal frame is not modal. User can press "Record" button in the main frame to start recording.<br>
<br>
<br>
<i>Virtual Music Note</i>

<<a href='http://lh5.ggpht.com/_BT5G2024qqo/TJYzeCk2YoI/AAAAAAAAACI/YR6ykbt0oEU/s600/GUI_3_csl.PNG'>http://lh5.ggpht.com/_BT5G2024qqo/TJYzeCk2YoI/AAAAAAAAACI/YR6ykbt0oEU/s600/GUI_3_csl.PNG</a>

Description:<br>
An internal frame that pops up together with Virtual Piano. As user pressed keys at Virtual Piano, a bar will be generated in Virtual Music Note. Color and length of bars depend on the octave selected and the time user pressed down a key.<br>
<br>
<br>
<i>Adding new instrument</i>

<a href='http://lh4.ggpht.com/_BT5G2024qqo/TJY6XX9I5OI/AAAAAAAAACc/4Vp1ZwcEx_8/s600/GUI_4_csl.PNG'>http://lh4.ggpht.com/_BT5G2024qqo/TJY6XX9I5OI/AAAAAAAAACc/4Vp1ZwcEx_8/s600/GUI_4_csl.PNG</a>

Description :<br>
An internal frame that pops up when user choose to add new instrument from Instrument(s) option the menu bar. This internal frame will link to Recording module. Unlike Visual Piano and Visual Music Note, this internal frame is modal.<br>
<br>
<br>
<b>Khoo Ka Thoong</b>

<img src='http://lh3.ggpht.com/_BT5G2024qqo/TJcsPMNTEsI/AAAAAAAAACw/zFouzJjydOs/s600/GUI%20%28Ka%20Thoong%29.jpg' />

<b>Wong Seok Yee</b>

<img src='http://lh3.ggpht.com/_BT5G2024qqo/TJgozKSKJZI/AAAAAAAAADQ/f4rC8Ujm6Ac/s600/img003.jpg' />

<img src='http://lh3.ggpht.com/_BT5G2024qqo/TJgoznW8FMI/AAAAAAAAADU/2viQ1ze_lO8/s600/img002.jpg' />

Description: GUI Design for advanced visual instrument<br>
<br>
<b>Lim Yeong Chuin</b>

<a href='http://lh6.ggpht.com/_jnNNAl2ZuXg/TJoK_FCresI/AAAAAAAAAA0/qy2uksHv9aM/s600/mainmenu.JPG'>http://lh6.ggpht.com/_jnNNAl2ZuXg/TJoK_FCresI/AAAAAAAAAA0/qy2uksHv9aM/s600/mainmenu.JPG</a>

<a href='http://lh5.ggpht.com/_jnNNAl2ZuXg/TJoK_lowF4I/AAAAAAAAAA8/aSdIGQH28Nw/s600/play.JPG'>http://lh5.ggpht.com/_jnNNAl2ZuXg/TJoK_lowF4I/AAAAAAAAAA8/aSdIGQH28Nw/s600/play.JPG</a>

Description: GUI for compose new music<br>
<br>
<a href='http://lh3.ggpht.com/_jnNNAl2ZuXg/TJoLALhgsRI/AAAAAAAAABA/m-3heQ0f88A/s600/mixing.JPG'>http://lh3.ggpht.com/_jnNNAl2ZuXg/TJoLALhgsRI/AAAAAAAAABA/m-3heQ0f88A/s600/mixing.JPG</a>

Description: Mixing different audio file into one<br>
<hr />