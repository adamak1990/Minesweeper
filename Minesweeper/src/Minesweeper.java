import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import javax.swing.*;

public class Minesweeper {

	Minesweeper game;
	boolean isSet, gameEnded, pauseGame, gameAborted;
	NewButton newGame, loadGame, initiate, reGame;
	JFrame frame;
	ButtonGroup difficulty;
	JRadioButton easy, medium, expert, custom;
	JPanel jradio, text, input, gameNorth;
	JTextField wText, hText, mText;
	public JLabel w, h, m, timerLabel, minesLeft, showRecords;
	int width, height, mines, heightM, widthM, minesAtStart, left, pixels;
	double score, loadedTime;
	long pauseTime, unpauseTime, toSubtract;
	String error, highscore, records;
	ArrayList<Double> easyHighscore, mediumHighscore, expertHighscore, highscoreInput;
	ArrayList<NewButton> buttons, toCheck;
	GridLayout grid;
	JPanel field;
	MouseListener bl, fl;
	ArrayList<Integer> chooseFrom, chosen, neighbours;
	SaveGame saveGame;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem menuItem1, menuItem2, menuItem3;
	ImageIcon flagIcon, mineIcon, blankIcon, num1, num2, num3, num4, num5, num6, num7, num8;
	JComboBox box;

	public static void main(String[] args) {
		Minesweeper game = new Minesweeper();
		game.preMainMenu();
	}

	public void preMainMenu() {
		newGame = new NewButton("New Game");
		loadGame = new NewButton("Load Game");
		newGame.addActionListener(new NewGameListener());
		loadGame.addActionListener(new LoadGameListener());
		pixels = 25;
		
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menuBar.add(menu);
		menuItem1 = new JMenuItem("Start New Game");
		menuItem2 = new JMenuItem("Highscores");
		menuItem3 = new JMenuItem("Close");
		menu.add(menuItem1);
		menu.add(menuItem2);
		menu.add(menuItem3);
		menuItem1.addActionListener(new MenuListener());
		menuItem2.addActionListener(new MenuListener());
		menuItem3.addActionListener(new MenuListener());
		
		flagIcon = new ImageIcon("images/flag.png");
		blankIcon = new ImageIcon("images/blank.png");
		mineIcon = new ImageIcon("images/mine.png");
		num1 = new ImageIcon("images/num1.png");
		num2 = new ImageIcon("images/num2.png");
		num3 = new ImageIcon("images/num3.png");
		num4 = new ImageIcon("images/num4.png");
		num5 = new ImageIcon("images/num5.png");
		num6 = new ImageIcon("images/num6.png");
		num7 = new ImageIcon("images/num7.png");
		num8 = new ImageIcon("images/num8.png");

		frame = new JFrame("(Almost) The Best Minesweeper Game");
		frame.add(newGame, BorderLayout.CENTER);
		frame.add(loadGame, BorderLayout.SOUTH);
		frame.setJMenuBar(menuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setResizable(false);
		frame.setVisible(true);

		try {
			FileInputStream fileStream = new FileInputStream("data/GameSet.sav");
			ObjectInputStream is = new ObjectInputStream(fileStream);
			saveGame = (SaveGame) is.readObject();
			is.close();
			width = saveGame.width;
			height = saveGame.height;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		File hScore = new File("data/Highscores.sav");
		if (!hScore.exists()) {
			new Highscore().go();
		}
		try {
			FileInputStream fileStream = new FileInputStream("data/Highscores.sav");
			ObjectInputStream is = new ObjectInputStream(fileStream);
			easyHighscore = (ArrayList<Double>) is.readObject();
			mediumHighscore = (ArrayList<Double>) is.readObject();
			expertHighscore = (ArrayList<Double>) is.readObject();
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println(easyHighscore);
		if (!(width > 0)) {
			loadGame.setEnabled(false);
		}
		System.out.println(easyHighscore);
		System.out.println(mediumHighscore);
		System.out.println(expertHighscore);
	}

	public void mainMenu() {

		frame.getContentPane().removeAll();
		easy = new JRadioButton("Easy - 9x9 grid, 10 mines");
		medium = new JRadioButton("Pro - 16x16 grid, 40 mines");
		expert = new JRadioButton("Expert - 30x16 grid, 99 mines");
		custom = new JRadioButton("Custom");
		easy.setSelected(true);
		easy.addActionListener(new CustomListener());
		medium.addActionListener(new CustomListener());
		expert.addActionListener(new CustomListener());
		custom.addActionListener(new CustomListener());

		difficulty = new ButtonGroup();
		difficulty.add(easy);
		difficulty.add(medium);
		difficulty.add(expert);
		difficulty.add(custom);

		jradio = new JPanel();
		jradio.setLayout(new BoxLayout(jradio, BoxLayout.Y_AXIS));
		jradio.add(easy);
		jradio.add(medium);
		jradio.add(expert);
		jradio.add(custom);

		w = new JLabel("Width (9-30): ");
		h = new JLabel("Height (9-24): ");
		m = new JLabel("Mines (10-667): ");

		wText = new JTextField("");
		hText = new JTextField("");
		mText = new JTextField("");
		wText.setEditable(false);
		hText.setEditable(false);
		mText.setEditable(false);

		input = new JPanel();
		input.setLayout(new BoxLayout(input, BoxLayout.Y_AXIS));
		input.add(w);
		input.add(wText);
		input.add(h);
		input.add(hText);
		input.add(m);
		input.add(mText);

		initiate = new NewButton("Start Game");
		initiate.setFont(new Font("SansSerif", Font.BOLD, 26));
		initiate.addActionListener(new InitiateListener());

		frame.add(initiate, BorderLayout.SOUTH);
		frame.add(input, BorderLayout.CENTER);
		frame.add(jradio, BorderLayout.NORTH);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}

	public void loadGame() {
		buttons = new ArrayList<NewButton>();
		try {
			FileInputStream fileStream = new FileInputStream("data/Game.sav");
			ObjectInputStream is = new ObjectInputStream(fileStream);
			buttons = (ArrayList<NewButton>) is.readObject();
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		File hScore = new File("data/Highscores.sav");
		if (!hScore.exists()) {
			new Highscore().go();
		}
		try {
			FileInputStream fileStream = new FileInputStream("data/Highscores.sav");
			ObjectInputStream is = new ObjectInputStream(fileStream);
			highscoreInput = (ArrayList<Double>) is.readObject();
			is.close();
			easyHighscore = highscoreInput;
			mediumHighscore = highscoreInput;
			expertHighscore = highscoreInput;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		pixels = saveGame.pixels;
		mines = saveGame.mines;
		left = saveGame.left;
		minesAtStart = saveGame.minesAtStart;

		frame.getContentPane().removeAll();
		minesLeft = new JLabel("Mines left: " + mines);
		timerLabel = new JLabel("Time: 0:00     ");
		gameNorth = new JPanel();
		gameNorth.add(timerLabel);
		gameNorth.add(minesLeft);
		grid = new GridLayout(saveGame.height, saveGame.width);
		grid.setHgap(1);
		grid.setVgap(1);
		field = new JPanel(grid);
		for (NewButton a : buttons) {
			field.add(a);
			if (a.isFlagged()) {
				fl = new FlagListener();
				a.addMouseListener(fl);
			} else {
				bl = new ButtonListener();
				a.addMouseListener(bl);
			}
		}

		isSet = true;
		gameEnded = false;
		frame.addWindowListener(new CloseFrameListener());
		frame.add(field, BorderLayout.CENTER);
		frame.add(gameNorth, BorderLayout.NORTH);
		frame.setSize(pixels * width, pixels * height + 40);
		loadedTime = saveGame.loadedTime;
		Time time = new Time();
		Thread timer = new Thread(time);
		timer.start();

		frame.setVisible(true);
	}

	public void saveGame() {
		try {
			FileOutputStream fileStream = new FileOutputStream("data/Game.sav");
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(buttons);
			objectStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		SaveGame saveGame = new SaveGame(highscore, width, height, mines, minesAtStart, left, pixels, score);
		try {
			FileOutputStream fileStream = new FileOutputStream("data/GameSet.sav");
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(saveGame);
			objectStream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void setData() {
		if (easy.isSelected()) {
			width = 9;
			height = 9;
			mines = 10;
			highscore = "easy";
			setUpGame();
		} else if (medium.isSelected()) {
			width = 16;
			height = 16;
			mines = 40;
			highscore = "medium";
			setUpGame();
		} else if (expert.isSelected()) {
			width = 30;
			height = 16;
			mines = 99;
			highscore = "expert";
			setUpGame();
		} else {
			width = Integer.parseInt(wText.getText());
			height = Integer.parseInt(hText.getText());
			mines = Integer.parseInt(mText.getText());

			if ((width > 8 && width < 31) && (height > 8 && height < 25)
					&& (mines > 9 && mines < (width - 1) * (height - 1) + 1)) {
				setUpGame();
			} else {
				error = "";
				if (width < 9 || width > 30) {
					error += "Width out of bounds. Choose beetween 9 and 30\n";
					if (width < 9) {
						widthM = 9;
						wText.setText("9");
					} else {
						widthM = 30;
						wText.setText("30");
					}
				}
				if (height < 9 || height > 24) {
					error += "Height out of bounds. Choose beetween 9 and 24\n";
					if (height < 9) {
						heightM = 9;
						hText.setText("9");
					} else {
						heightM = 24;
						hText.setText("24");
					}
				}
				if (mines < 10 || mines > (widthM - 1) * (heightM - 1)) {
					if ((width > 8 && width < 31) && (height > 8 && height < 25)) {
						widthM = width;
						heightM = height;
						error += "Mines range depends on size. For current size choose beetween 10 and "
								+ (width - 1) * (height - 1);
						if (mines < 10) {
							mText.setText("10");
						} else {
							int text = (widthM - 1) * (heightM - 1);
							mText.setText(Integer.toString(text));
						}
					} else {
						error += "Mines out of bounds. Choose beetween 10 and 667";
						if (mines < 10) {
							mText.setText("10");
						} else {
							int text = (widthM - 1) * (heightM - 1);
							mText.setText(Integer.toString(text));
						}
					}
				}
				JOptionPane.showMessageDialog(null, error);
				width = 0;
				height = 0;
				mines = 0;
				heightM = 0;
				widthM = 0;
			}
		}
		minesAtStart = mines;
		left = width * height;
	}

	public void setUpGame() {
		frame.getContentPane().removeAll();
		timerLabel = new JLabel("Time: 0:00     ");
		minesLeft = new JLabel("Mines left: " + mines);
		gameNorth = new JPanel();
		gameNorth.add(timerLabel);
		gameNorth.add(minesLeft);

		buttons = new ArrayList<NewButton>();
		grid = new GridLayout(height, width);
		grid.setHgap(1);
		grid.setVgap(1);
		field = new JPanel(grid);
		for (int i = 0; i < width * height; i++) {
			NewButton a = new NewButton();
			buttons.add(a);
			field.add(a);
			bl = new ButtonListener();
			a.addMouseListener(bl);
		}
		frame.addWindowListener(new CloseFrameListener());
		frame.add(field, BorderLayout.CENTER);
		frame.add(gameNorth, BorderLayout.NORTH);
		frame.setSize(pixels * width, pixels * height + 40);
		frame.setVisible(true);
	}

	public void reveal(NewButton b) {
		if (b.isMine) {
			bust();
		} else if (b.bombNeighbours > 0) {
			if (b.isEnabled() && !b.isFlagged()) {
				displayNum(b);
				b.setEnabled(false);
				b.deFlag();
				left--;
				updateMines();
			}
		} else {
			if (b.isEnabled()) {
				displayNum(b);
				b.setEnabled(false);
				b.deFlag();
				left--;
				updateMines();
			}
			neighbours(buttons.indexOf(b));
			for (int a : neighbours) {
				if (buttons.get(a).isEnabled()) {
					displayNum(buttons.get(a));
					buttons.get(a).setEnabled(false);
					buttons.get(a).deFlag();
					left--;
					updateMines();
					if (buttons.get(a).bombNeighbours > 0) {
						displayNum(buttons.get(a));
					} else {
						reveal(buttons.get(a));
					}
				}
			}
		}
	}

	public void bust() {
		gameEnded = true;
		for (NewButton b : buttons) {
			if (b.isMine) {
				b.setIcon(mineIcon);
				b.setDisabledIcon(mineIcon);
			} else if (b.bombNeighbours > 0) {
				displayNum(b);
			} else if (b.bombNeighbours == 0) {
				displayNum(b);
			}
			b.setEnabled(false);
			b.deFlag();
		}
		JOptionPane.showOptionDialog(null, "Przegrałeś, Ty Lamusie", "Przegrana", JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE, null, null, null);
		setUpNextGame();
	}

	public void setUpNextGame() {
		gameAborted = false;
		isSet = false;
		gameEnded = false;
		frame.getContentPane().removeAll();
		reGame = new NewButton("Play New Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(reGame);
		reGame.addActionListener(new NewGameListener());
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public NewButton getButton(int x, int y) {
		int index = x + width * (y - 1) - 1;
		return buttons.get(index);
	}

	public int getIndex(int x, int y) {
		return x + width * (y - 1) - 1;
	}

	public int getX(NewButton b) {
		int index = buttons.indexOf(b);
		return (index) % width + 1;
	}

	public int getX(int index) {
		return (index) % width + 1;
	}

	public int getY(NewButton b) {
		double index = buttons.indexOf(b);
		return (int) ((index / width + 1));
	}

	public int getY(int index) {
		return (int) (int) ((index / width + 1));
	}

	public void neighbours(int index) {
		int x = getX(index);
		int y = getY(index);
		neighbours = new ArrayList<Integer>();
		if (x > 1 && x < width && y > 1 && y < height) {
			neighbours.add(index + width + 1);
			neighbours.add(index + width);
			neighbours.add(index + width - 1);
			neighbours.add(index + 1);
			neighbours.add(index);
			neighbours.add(index - 1);
			neighbours.add(index - width + 1);
			neighbours.add(index - width);
			neighbours.add(index - width - 1);
		} else if (x == 1 && y == 1) {
			neighbours.add(index + width + 1);
			neighbours.add(index + width);
			neighbours.add(index + 1);
			neighbours.add(index);
		} else if (x == width && y == height) {
			neighbours.add(index);
			neighbours.add(index - 1);
			neighbours.add(index - width);
			neighbours.add(index - width - 1);
		} else if (x == 1 && y == height) {
			neighbours.add(index + 1);
			neighbours.add(index);
			neighbours.add(index - width + 1);
			neighbours.add(index - width);
		} else if (x == width && y == 1) {
			neighbours.add(index + width);
			neighbours.add(index + width - 1);
			neighbours.add(index);
			neighbours.add(index - 1);
		} else if (x == 1) {
			neighbours.add(index + width + 1);
			neighbours.add(index + width);
			neighbours.add(index + 1);
			neighbours.add(index);
			neighbours.add(index - width + 1);
			neighbours.add(index - width);
		} else if (x == width) {
			neighbours.add(index + width);
			neighbours.add(index + width - 1);
			neighbours.add(index);
			neighbours.add(index - 1);
			neighbours.add(index - width);
			neighbours.add(index - width - 1);
		} else if (y == 1) {
			neighbours.add(index + width + 1);
			neighbours.add(index + width);
			neighbours.add(index + width - 1);
			neighbours.add(index + 1);
			neighbours.add(index);
			neighbours.add(index - 1);
		} else if (y == height) {
			neighbours.add(index + 1);
			neighbours.add(index);
			neighbours.add(index - 1);
			neighbours.add(index - width + 1);
			neighbours.add(index - width);
			neighbours.add(index - width - 1);
		}
	}

	public void displayNum(NewButton b) {
		if (!b.isMine) {
			switch (b.bombNeighbours) {
			case 0:
				b.setIcon(blankIcon);
				b.setDisabledIcon(blankIcon);
				break;
			case 1:
				b.setIcon(num1);
				b.setDisabledIcon(num1);
				break;
			case 2:
				b.setIcon(num2);
				b.setDisabledIcon(num2);
				break;
			case 3:
				b.setIcon(num3);
				b.setDisabledIcon(num3);
				break;
			case 4:
				b.setIcon(num4);
				b.setDisabledIcon(num4);
				break;
			case 5:
				b.setIcon(num5);
				b.setDisabledIcon(num5);
				break;
			case 6:
				b.setIcon(num6);
				b.setDisabledIcon(num6);
				break;
			case 7:
				b.setIcon(num7);
				b.setDisabledIcon(num7);
				break;
			case 8:
				b.setIcon(num8);
				b.setDisabledIcon(num8);
				break;
			default:
				break;
			}
		}
	}
	public void updateMines() {
		mines = minesAtStart;
		for(NewButton a: buttons) {
			if (a.isFlagged()){
				mines--;
			}
		}
		minesLeft.setText("Mines left:" + mines);
	}

	public class NewGameListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			mainMenu();
		}
	}

	public class LoadGameListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			loadGame();
		}
	}

	public class CustomListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			if (custom.isSelected()) {
				wText.setEditable(true);
				hText.setEditable(true);
				mText.setEditable(true);
			} else {
				wText.setEditable(false);
				hText.setEditable(false);
				mText.setEditable(false);
			}
		}
	}

	public class InitiateListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {

			File file = new File("data/GameSet.sav");
			try {
				Files.deleteIfExists(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			setData();
		}
	}

	public class ButtonListener implements MouseListener {

		public void mouseClicked(MouseEvent m) {
		}

		public void mouseEntered(MouseEvent m) {
		}

		public void mouseExited(MouseEvent m) {
		}

		public void mousePressed(MouseEvent m) {

			if (m.getButton() == MouseEvent.BUTTON1 && !isSet) {
				isSet = true;
				displayNum((NewButton) m.getComponent());
				m.getComponent().setEnabled(false);
				((NewButton)(m.getComponent())).deFlag();
				left--;
				updateMines();
				chooseFrom = new ArrayList<Integer>();
				chosen = new ArrayList<Integer>();
				int index = buttons.indexOf(m.getComponent());
				for (int i = 0; i < width * height; i++) {
					chooseFrom.add(i);
				}
				neighbours(index);
				for (int i : neighbours) {
					chooseFrom.remove(i);
				}
				neighbours = null;
				for (int i = 0; i < mines; i++) {
					int inde = (int) (Math.random() * chooseFrom.size());
					chosen.add(chooseFrom.get(inde));
					buttons.get(chooseFrom.get(inde)).isMine = true;
					chooseFrom.remove(chooseFrom.get(inde));
				}
				for (NewButton b : buttons) {
					if (!b.isMine) {

						int inde = buttons.indexOf(b);
						int count = 0;
						neighbours(inde);
						for (int i : neighbours) {
							if (buttons.get(i).isMine == true) {
								count++;
							}
							b.bombNeighbours = count;
						}
						neighbours = null;
					}
				}
				reveal((NewButton) (m.getComponent()));
				Time time = new Time();
				Thread timer = new Thread(time);
				timer.start();
			} else if (isSet && m.getButton() == MouseEvent.BUTTON1 && m.getComponent().isEnabled()) {
				NewButton butt = (NewButton) (m.getComponent());
				if (!butt.isFlagged()) {
					reveal((NewButton) (m.getComponent()));
				}
			} else if (isSet && m.getButton() == MouseEvent.BUTTON3) {
				if (m.getComponent().isEnabled()) {
					NewButton flag = (NewButton) (m.getComponent());
					if (!flag.isFlagged()) {
						flag.setFlagged();
						flag.setIcon(flagIcon);
						flag.done = true;
						mines--;
						minesLeft.setText("Mines left: " + mines);
						flag.removeMouseListener(bl);
						fl = new FlagListener();
						flag.addMouseListener(fl);
					}
				}
			}
			if (minesAtStart == left) {
				gameEnded = true;
				String information = "";
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (score > 0) {
					Highscore highscoreOutput = new Highscore(easyHighscore, mediumHighscore, expertHighscore);

					if (highscore == "easy") {
						for (Double el : easyHighscore) {
							if (score < el) {
								if (easyHighscore.indexOf(el)==0) {
									information = "Contratulations, you beat the Highscore \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += scoreMinutes + ":" + scoreSeconds;
								} else if  (easyHighscore.indexOf(el)>0) {
									String th = "th";
									if (easyHighscore.indexOf(el)==1) {th = "nd";}
									if (easyHighscore.indexOf(el)==2) {th = "rd";}
									information = "Contratulations, you achieved " + (easyHighscore.indexOf(el)+1) + th + " result \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += scoreMinutes + ":" + scoreSeconds;
								}
								easyHighscore.add(easyHighscore.indexOf(el), new Double(score));
								break;
							}
						}
						if (easyHighscore.size() > 5) {
							easyHighscore.remove(5);
						}
						highscoreOutput.easy = easyHighscore;
						System.out.println(easyHighscore);

					} else if (highscore == "medium") {
						for (Double el : mediumHighscore) {
							if (score < el) {
								if (mediumHighscore.indexOf(el)==0) {
									information = "Contratulations, you beat the Highscore \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += scoreMinutes + ":" + scoreSeconds;
								} else if  (mediumHighscore.indexOf(el)>0) {
									String th = "th";
									if (mediumHighscore.indexOf(el)==1) {th = "nd";}
									if (mediumHighscore.indexOf(el)==2) {th = "rd";}
									information = "Contratulations, you achieved " + (mediumHighscore.indexOf(el)+1) + th + " result \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += scoreMinutes + ":" + scoreSeconds;
								}
								mediumHighscore.add(mediumHighscore.indexOf(el), new Double(score));
								break;
							}
						}
						if (mediumHighscore.size() > 5) {
							mediumHighscore.remove(5);
						}
						highscoreOutput.medium = mediumHighscore;
						System.out.println(mediumHighscore);
						
					} else if (highscore == "expert") {
						for (Double el : expertHighscore) {
							if (score < el) {
								if (expertHighscore.indexOf(el)==0) {
									information = "Contratulations, you beat the Highscore \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += scoreMinutes + ":" + scoreSeconds;
								} else if  (expertHighscore.indexOf(el)>0) {
									String th = "th";
									if (expertHighscore.indexOf(el)==1) {th = "nd";}
									if (expertHighscore.indexOf(el)==2) {th = "rd";}
									information = "Contratulations, you achieved " + (expertHighscore.indexOf(el)+1) + th + " result \nYour score is: ";
									int scoreSeconds = (int) (score % 60);
									int scoreMinutes = (int) (score / 60) % 60;
									information += scoreMinutes + ":" + scoreSeconds;
								}
								expertHighscore.add(expertHighscore.indexOf(el), new Double(score));
								break;
							}
						}
						if (expertHighscore.size() > 5) {
							expertHighscore.remove(5);
						}
						highscoreOutput.expert = expertHighscore;
						System.out.println(expertHighscore);
					try {
						FileOutputStream fileStream = new FileOutputStream("data/Highscores.sav");
						ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
						objectStream.writeObject(highscoreOutput.easy);
						objectStream.writeObject(highscoreOutput.medium);
						objectStream.writeObject(highscoreOutput.expert);
						objectStream.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}

				JOptionPane.showOptionDialog(null, information, "Wygrana!", JOptionPane.DEFAULT_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null, null, null);
				setUpNextGame();
			}}
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	public class FlagListener implements MouseListener {
		public void mouseClicked(MouseEvent m) {
		}

		public void mouseEntered(MouseEvent m) {
		}

		public void mouseExited(MouseEvent m) {
		}

		public void mousePressed(MouseEvent m) {
			if (isSet && m.getButton() == MouseEvent.BUTTON3) {
				if (m.getComponent().isEnabled()) {
					NewButton flag = (NewButton) (m.getComponent());
					if (flag.isFlagged()) {
						flag.deFlag();
						flag.setIcon(null);
						flag.done = true;
						mines++;
						minesLeft.setText("Mines left: " + mines);
						flag.removeMouseListener(fl);
						flag.addMouseListener(bl);
					}
				}
			}
		}

		public void mouseReleased(MouseEvent m) {
		}
	}

	public class CloseFrameListener implements WindowListener {

		public void windowActivated(WindowEvent arg0) {
		}

		public void windowClosed(WindowEvent arg0) {
		}

		public void windowClosing(WindowEvent arg0) {
			if (isSet) {
				pauseGame = true;
				pauseTime = System.currentTimeMillis();
				frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				int result = JOptionPane.showConfirmDialog(null, "Save game?", "Do you want to save game",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (result == JOptionPane.YES_OPTION) {
					toSubtract += unpauseTime - pauseTime;
					saveGame();
					System.exit(0);
				} else if (result == JOptionPane.NO_OPTION) {
					File file = new File("data/GameSet.sav");
					try {
						Files.deleteIfExists(file.toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.exit(0);
				} else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
					unpauseTime = System.currentTimeMillis();
				}
				toSubtract += unpauseTime - pauseTime;
				pauseGame = false;
			}

		}

		public void windowDeactivated(WindowEvent arg0) {
		}

		public void windowDeiconified(WindowEvent arg0) {
		}

		public void windowIconified(WindowEvent arg0) {
		}

		public void windowOpened(WindowEvent arg0) {
		}
	}
	public class MenuListener implements ActionListener {
		public void actionPerformed (ActionEvent a) {
			if (a.getSource()== menuItem1) {
				if (isSet) {
					pauseGame = true;
					pauseTime = System.currentTimeMillis();
					int result = JOptionPane.showConfirmDialog(null, "Start new game?", "Do you want to start new game",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (result == JOptionPane.YES_OPTION) {
						toSubtract = 0;
						File file = new File("data/GameSet.sav");
						try {
							Files.deleteIfExists(file.toPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
						gameEnded = true;
						
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						setUpNextGame();
						mainMenu();
					} else if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
						unpauseTime = System.currentTimeMillis();
						toSubtract += unpauseTime - pauseTime;
					}

					pauseGame = false;
				} else {
					File file = new File("data/GameSet.sav");
					try {
						Files.deleteIfExists(file.toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
					gameEnded = true;
					
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					setUpNextGame();
					mainMenu();
				}
				
			} else if (a.getSource()== menuItem2) {
				String[] string = {"Easy", "Pro", "Expert"};
				
				
				box = new JComboBox(string);
				box.addActionListener(new ComboboxListener());

				String hint = "Choose difficulty level";
				
				records = "<html>Best results for Easy level: <br>" ;
				for (Double el: easyHighscore) {
					if (el < 99999){
						int scoreSeconds = (int) (el % 60);
						int scoreMinutes = (int) (el / 60) % 60;
						int place = easyHighscore.indexOf(el)+1;
						records += place + "<html>:      " + scoreMinutes + ":" + scoreSeconds + "<br>";
				} else {break;}
				}
				records += "</html>";
				showRecords = new JLabel(records);
				
				Object[] things = new Object[3];
				things[0] = hint;
				things[1] = box;
				things[2] = showRecords;

				JOptionPane dialog = new JOptionPane();
				dialog.showMessageDialog(null, things);
				
			} else if (a.getSource()== menuItem3) {
				if (isSet) {
					pauseGame = true;
					pauseTime = System.currentTimeMillis();
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					int result = JOptionPane.showConfirmDialog(null, "Save game?", "Do you want to save game",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

					if (result == JOptionPane.YES_OPTION) {
						toSubtract += unpauseTime - pauseTime;
						saveGame();
						System.exit(0);
					} else if (result == JOptionPane.NO_OPTION) {
						File file = new File("data/GameSet.sav");
						try {
							Files.deleteIfExists(file.toPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.exit(0);
					} else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION) {
						unpauseTime = System.currentTimeMillis();
					}
					toSubtract += unpauseTime - pauseTime;
					pauseGame = false;
				} else {
					System.exit(0);
				}}}
	
	}
	public class ComboboxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (box.getSelectedIndex() == 0) {
				records = "<html>Best results for Easy level: <br>" ;
				for (Double el: easyHighscore) {
					if (el < 99999){
						int scoreSeconds = (int) (el % 60);
						int scoreMinutes = (int) (el / 60) % 60;
						int place = easyHighscore.indexOf(el)+1;
						records += place + "<html>:      " + scoreMinutes + ":" + scoreSeconds + "<br>";
				} else {break;}
				}
				records += "</html>";
				showRecords.setText(records);
			} else if (box.getSelectedIndex() == 1) {
				records = "<html>Best results for Pro level: <br>" ;
				for (Double el: mediumHighscore) {
					if (el < 99999){
						int scoreSeconds = (int) (el % 60);
						int scoreMinutes = (int) (el / 60) % 60;
						int place = mediumHighscore.indexOf(el)+1;
						records += place + "<html>:      " + scoreMinutes + ":" + scoreSeconds + "<br>";
				} else {break;}
				}
				records += "</html>";
				showRecords.setText(records);
			} else if (box.getSelectedIndex() == 2) {
				records = "<html>Best results for Expert level: <br>" ;
				for (Double el: expertHighscore) {
					if (el < 99999){
						int scoreSeconds = (int) (el % 60);
						int scoreMinutes = (int) (el / 60) % 60;
						int place = expertHighscore.indexOf(el)+1;
						records += place + "<html>:      " + scoreMinutes + ":" + scoreSeconds + "<br>";
				} else {break;}
				}
				records += "</html>";
				showRecords.setText(records);
			}
		}
	}


	public class Time implements Runnable {
		long startTime, currentTime;
		double currentTimer;
		int timerSeconds, timerMinutes, timerHours;

		public void run() {
			go();
		}
 
		public void go() {
			startTime = System.currentTimeMillis();

			while (!gameEnded) {
				currentTime = System.currentTimeMillis();
				if (!pauseGame) {
					timerLabel.setText(String.format("Time:  %d:%02d     ", timerMinutes, timerSeconds));
					currentTimer = (currentTime - startTime - toSubtract) / 1000.0  + loadedTime;
					timerSeconds = (int) (currentTimer % 60);
					timerMinutes = (int) (currentTimer / 60) % 60;
					score = currentTimer;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

}
