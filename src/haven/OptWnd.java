/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import java.awt.event.KeyEvent;
import java.util.*;
import java.awt.font.TextAttribute;
import java.awt.*;

public class OptWnd extends Window {
	private static final Text.Foundry sectionfndr = new Text.Foundry(Text.dfont.deriveFont(Font.BOLD, Text.cfg.label));
	public static final int VERTICAL_MARGIN = 10;
	public static final int HORIZONTAL_MARGIN = 5;
	public static final int VERTICAL_AUDIO_MARGIN = 5;
	public final Panel main, video, audio, display, map, general, combat, control, mapping, uis, quality, flowermenus, soundalarms, keybind;
	public Panel current;

    public void chpanel(Panel p) {
	Coord cc = this.c.add(this.sz.div(2));
	if(current != null)
	    current.hide();
	(current = p).show();
	pack();
	move(cc.sub(this.sz.div(2)));
    }

    public class PButton extends Button {
	public final Panel tgt;
	public final int key;

	public PButton(int w, String title, int key, Panel tgt) {
	    super(w, title);
	    this.tgt = tgt;
	    this.key = key;
	}

	public void click() {
	    chpanel(tgt);
	}

	public boolean keydown(java.awt.event.KeyEvent ev) {
	    if((this.key != -1) && (ev.getKeyChar() == this.key)) {
		click();
		return(true);
	    }
	    return(false);
	}
    }

    public class Panel extends Widget {
	public Panel() {
	    visible = false;
	    c = Coord.z;
	}
    }

	public class VideoPanel extends Panel {
		public VideoPanel(Panel back) {
			super();
			add(new PButton(200, "Back", 27, back), new Coord(210, 360));
			resize(new Coord(620, 400));
		}

		public class CPanel extends Widget {
			public GSettings prefs;

			public CPanel(GSettings gprefs) {
				this.prefs = gprefs;
				final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(this, new Coord(620, 350)));
				appender.setVerticalMargin(VERTICAL_MARGIN);
				appender.setHorizontalMargin(HORIZONTAL_MARGIN);

				appender.add(new CheckBox("Disable biome tile transitions (requires logout)") {
					{
						a = Config.disabletiletrans;
					}
					public void set(boolean val) {
						Config.disabletiletrans = val;
						Utils.setprefb("disabletiletrans", val);
						a = val;
					}
				});
				appender.add(new CheckBox("Disable terrain smoothing (requires logout)") {
					{
						a = Config.disableterrainsmooth;
					}
					public void set(boolean val) {
						Config.disableterrainsmooth = val;
						Utils.setprefb("disableterrainsmooth", val);
						a = val;
					}
				});
				appender.add(new CheckBox("Disable terrain elevation (requires logout)") {
					{
						a = Config.disableelev;
					}
					public void set(boolean val) {
						Config.disableelev = val;
						Utils.setprefb("disableelev", val);
						a = val;
					}
				});
				appender.add(new CheckBox("Disable flavor objects including ambient sounds") {
					{
						a = Config.hideflocomplete;
					}

					public void set(boolean val) {
						Utils.setprefb("hideflocomplete", val);
						Config.hideflocomplete = val;
						a = val;
					}
				});
				appender.add(new CheckBox("Hide flavor objects but keep sounds (requires logout)") {
					{
						a = Config.hideflovisual;
					}

					public void set(boolean val) {
						Utils.setprefb("hideflovisual", val);
						Config.hideflovisual = val;
						a = val;
					}
				});
				appender.add(new CheckBox("Show weather") {
					{
						a = Config.showweather;
					}

					public void set(boolean val) {
						Utils.setprefb("showweather", val);
						Config.showweather = val;
						a = val;
					}
				});
				appender.add(new CheckBox("Simple crops (req. logout)") {
					{
						a = Config.simplecrops;
					}

					public void set(boolean val) {
						Utils.setprefb("simplecrops", val);
						Config.simplecrops = val;
						a = val;
					}
				});
				appender.add(new CheckBox("Hide crops") {
					{
						a = Config.hidecrops;
					}

					public void set(boolean val) {
						Utils.setprefb("hidecrops", val);
						Config.hidecrops = val;
						a = val;
					}
				});
				appender.add(new CheckBox("smooth snow in minimap") {
					{
						a = Config.minimapsmooth;
					}

					public void set(boolean val) {
						Utils.setprefb("minimapsmooth", val);
						Config.minimapsmooth = val;
						a = val;
					}
				});
				appender.add(new CheckBox("Show FPS") {
					{
						a = Config.showfps;
					}

					public void set(boolean val) {
						Utils.setprefb("showfps", val);
						Config.showfps = val;
						a = val;
					}
				});
				appender.add(new Label("Disable animations (req. restart):"));
				CheckListbox disanimlist = new CheckListbox(320, Math.min(8, Config.disableanim.values().size()), 18 + Config.fontadd) {
					@Override
					protected void itemclick(CheckListboxItem itm, int button) {
						super.itemclick(itm, button);
						Utils.setprefchklst("disableanim", Config.disableanim);
					}
				};
				for (CheckListboxItem itm : Config.disableanim.values())
					disanimlist.items.add(itm);
				appender.add(disanimlist);
				
				pack();
			}
		}

		private CPanel curcf = null;

		public void draw(GOut g) {
			if ((curcf == null) || (ui.gprefs != curcf.prefs)) {
				if (curcf != null)
					curcf.destroy();
				curcf = add(new CPanel(ui.gprefs), Coord.z);
			}
			super.draw(g);
		}
	}


	private void error(String msg) {
	GameUI gui = getparent(GameUI.class);
	if(gui != null)
	    gui.error(msg);
    }

    private static final Text kbtt = RichText.render("$col[255,255,0]{Escape}: Cancel input\n" +
						     "$col[255,255,0]{Backspace}: Revert to default\n" +
						     "$col[255,255,0]{Delete}: Disable keybinding", 0);
    public class BindingPanel extends Panel {
	private int addbtn(Widget cont, String nm, KeyBinding cmd, int y) {
	    Widget btn = cont.add(new SetButton(175, cmd), 100, y);
	    cont.adda(new Label(nm), 0, y + (btn.sz.y / 2), 0, 0.5);
	    return(y + 30);
	}

	public BindingPanel(Panel back) {
	    super();
	    Widget cont = add(new Scrollport(new Coord(300, 300))).cont;
	    int y = 0;
	    cont.adda(new Label("Main menu"), cont.sz.x / 2, y, 0.5, 0); y += 20;
	    y = addbtn(cont, "Inventory", GameUI.kb_inv, y);
	    y = addbtn(cont, "Equipment", GameUI.kb_equ, y);
	    y = addbtn(cont, "Character sheet", GameUI.kb_chr, y);
	    y = addbtn(cont, "Map window", GameUI.kb_map, y);
	    y = addbtn(cont, "Kith & Kin", GameUI.kb_bud, y);
	    y = addbtn(cont, "Options", GameUI.kb_opt, y);
	    y = addbtn(cont, "Search actions", GameUI.kb_srch, y);
	    y = addbtn(cont, "Toggle chat", GameUI.kb_chat, y);
	    y = addbtn(cont, "Quick chat", ChatUI.kb_quick, y);
	    y = addbtn(cont, "Display claims", GameUI.kb_claim, y);
	    y = addbtn(cont, "Display villages", GameUI.kb_vil, y);
	    y = addbtn(cont, "Display realms", GameUI.kb_rlm, y);
	    y = addbtn(cont, "Take screenshot", GameUI.kb_shoot, y);
	    y = addbtn(cont, "Toggle UI", GameUI.kb_hide, y);
	    y += 10;
	    cont.adda(new Label("Camera control"), cont.sz.x / 2, y, 0.5, 0); y += 20;
	    y = addbtn(cont, "Rotate left", MapView.kb_camleft, y);
	    y = addbtn(cont, "Rotate right", MapView.kb_camright, y);
	    y = addbtn(cont, "Zoom in", MapView.kb_camin, y);
	    y = addbtn(cont, "Zoom out", MapView.kb_camout, y);
	    y = addbtn(cont, "Reset", MapView.kb_camreset, y);
	    y += 10;
	    cont.adda(new Label("Walking speed"), cont.sz.x / 2, y, 0.5, 0); y += 20;
	    y = addbtn(cont, "Increase speed", Speedget.kb_speedup, y);
	    y = addbtn(cont, "Decrease speed", Speedget.kb_speeddn, y);
	    for(int i = 0; i < 4; i++)
		y = addbtn(cont, String.format("Set speed %d", i + 1), Speedget.kb_speeds[i], y);
	    y += 10;
	    cont.adda(new Label("Combat actions"), cont.sz.x / 2, y, 0.5, 0); y += 20;
	    for(int i = 0; i < Fightsess.kb_acts.length; i++)
		y = addbtn(cont, String.format("Combat action %d", i + 1), Fightsess.kb_acts[i], y);
	    y = addbtn(cont, "Switch targets", Fightsess.kb_relcycle, y);
	    y += 10;
	    y = cont.sz.y + 10;
	    adda(new PointBind(200), cont.sz.x / 2, y, 0.5, 0); y += 30;
	    adda(new PButton(200, "Back", 27, back), cont.sz.x / 2, y, 0.5, 0); y += 30;
	    pack();
	}

	public class SetButton extends KeyMatch.Capture {
	    public final KeyBinding cmd;

	    public SetButton(int w, KeyBinding cmd) {
		super(w, cmd.key());
		this.cmd = cmd;
	    }

	    public void set(KeyMatch key) {
		super.set(key);
		cmd.set(key);
	    }

	    protected KeyMatch mkmatch(KeyEvent ev) {
		return(KeyMatch.forevent(ev, ~cmd.modign));
	    }

	    protected boolean handle(KeyEvent ev) {
		if(ev.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
		    cmd.set(null);
		    super.set(cmd.key());
		    return(true);
		}
		return(super.handle(ev));
	    }

	    public Object tooltip(Coord c, Widget prev) {
		return(kbtt.tex());
	    }
	}
    }


    public static class PointBind extends Button {
	public static final String msg = "Bind other elements...";
	public static final Resource curs = Resource.local().loadwait("gfx/hud/curs/wrench");
	private UI.Grab mg, kg;
	private KeyBinding cmd;

	public PointBind(int w) {
	    super(w, msg, false);
	    tooltip = RichText.render("Bind a key to an element not listed above, such as an action-menu " +
				      "button. Click the element to bind, and then press the key to bind to it. " +
				      "Right-click to stop rebinding.",
				      300);
	}

	public void click() {
	    if(mg == null) {
		change("Click element...");
		mg = ui.grabmouse(this);
	    } else if(kg != null) {
		kg.remove();
		kg = null;
		change(msg);
	    }
	}

	private boolean handle(KeyEvent ev) {
	    switch(ev.getKeyCode()) {
	    case KeyEvent.VK_SHIFT: case KeyEvent.VK_CONTROL: case KeyEvent.VK_ALT:
	    case KeyEvent.VK_META: case KeyEvent.VK_WINDOWS:
		return(false);
	    }
	    int code = ev.getKeyCode();
	    if(code == KeyEvent.VK_ESCAPE) {
		return(true);
	    }
	    if(code == KeyEvent.VK_BACK_SPACE) {
		cmd.set(null);
		return(true);
	    }
	    if(code == KeyEvent.VK_DELETE) {
		cmd.set(KeyMatch.nil);
		return(true);
	    }
	    KeyMatch key = KeyMatch.forevent(ev, ~cmd.modign);
	    if(key != null)
		cmd.set(key);
	    return(true);
	}

	public boolean mousedown(Coord c, int btn) {
	    if(mg == null)
		return(super.mousedown(c, btn));
	    Coord gc = ui.mc;
	    if(btn == 1) {
		this.cmd = KeyBinding.Bindable.getbinding(ui.root, gc);
		return(true);
	    }
	    if(btn == 3) {
		mg.remove();
		mg = null;
		change(msg);
		return(true);
	    }
	    return(false);
	}

	public boolean mouseup(Coord c, int btn) {
	    if(mg == null)
		return(super.mouseup(c, btn));
	    Coord gc = ui.mc;
	    if(btn == 1) {
		if((this.cmd != null) && (KeyBinding.Bindable.getbinding(ui.root, gc) == this.cmd)) {
		    mg.remove();
		    mg = null;
		    kg = ui.grabkeys(this);
		    change("Press key...");
		} else {
		    this.cmd = null;
		}
		return(true);
	    }
	    if(btn == 3)
		return(true);
	    return(false);
	}

	public Resource getcurs(Coord c) {
	    if(mg == null)
		return(null);
	    return(curs);
	}

	public boolean keydown(KeyEvent ev) {
	    if(kg == null)
		return(super.keydown(ev));
	    if(handle(ev)) {
		kg.remove();
		kg = null;
		cmd = null;
		change("Click another element...");
		mg = ui.grabmouse(this);
	    }
	    return(true);
	}
    }

    public OptWnd(boolean gopts) {
	super(Coord.z, "Options", true);
	main = add(new Panel());
	video = add(new VideoPanel(main));
	audio = add(new Panel());
	display = add(new Panel());
	map = add(new Panel());
	general = add(new Panel());
	combat = add(new Panel());
	control = add(new Panel());
	mapping = add(new Panel());
	uis = add(new Panel());
	quality = add(new Panel());
	flowermenus = add(new Panel());
	soundalarms = add(new Panel());
	keybind = add(new BindingPanel(main));
	int y;

	main.add(new PButton(200, "Video settings", 'v', video), new Coord(0, 0));
	main.add(new PButton(200, "Audio settings", 'a', audio), new Coord(0, 30));
	main.add(new PButton(200, "Keybindings", 'k', keybind), new Coord(0, 60));
	main.add(new PButton(200, "Display settings", 'd', display), new Coord(0, 90));
	if(gopts) {
	    main.add(new Button(200, "Switch character") {
		    public void click() {
			getparent(GameUI.class).act("lo", "cs");
		    }
		}, new Coord(0, 120));
	    main.add(new Button(200, "Log out") {
		    public void click() {
			getparent(GameUI.class).act("lo");
		    }
		}, new Coord(0, 150));
	}
	main.add(new Button(200, "Close") {
		public void click() {
		    OptWnd.this.hide();
		}
	    }, new Coord(0, 180));
	main.pack();

	y = 0;
	audio.add(new Label("Master audio volume"), new Coord(0, y));
	y += 15;
	audio.add(new HSlider(200, 0, 1000, (int)(Audio.volume * 1000)) {
		public void changed() {
		    Audio.setvolume(val / 1000.0);
		}
	    }, new Coord(0, y));
	y += 30;
	audio.add(new Label("In-game event volume"), new Coord(0, y));
	y += 15;
	audio.add(new HSlider(200, 0, 1000, 0) {
		protected void attach(UI ui) {
		    super.attach(ui);
		    val = (int)(ui.audio.pos.volume * 1000);
		}
		public void changed() {
		    ui.audio.pos.setvolume(val / 1000.0);
		}
	    }, new Coord(0, y));
	y += 20;
	audio.add(new Label("Ambient volume"), new Coord(0, y));
	y += 15;
	audio.add(new HSlider(200, 0, 1000, 0) {
		protected void attach(UI ui) {
		    super.attach(ui);
		    val = (int)(ui.audio.amb.volume * 1000);
		}
		public void changed() {
		    ui.audio.amb.setvolume(val / 1000.0);
		}
	    }, new Coord(0, y));
	y += 35;
	audio.add(new PButton(200, "Back", 27, main), new Coord(0, 180));
	audio.pack();

	// -------------------------------------------- display
	y = 0;
	display.add(new CheckBox("Show flavor objects") {
		{a = Utils.getprefb("showflo", true);}

		public void set(boolean val) {
			if (val) {
				Utils.setprefb("showflo", true);
			} else {
				Utils.setprefb("showflo", false);
			}
			a = val;
		}
	}, new Coord(0, y));

	display.add(new PButton(200, "Back", 27, main), new Coord(0, 180));
	display.pack();

	chpanel(main);
    }

	static private Scrollport.Scrollcont withScrollport(Widget widget, Coord sz) {
		final Scrollport scroll = new Scrollport(sz);
		widget.add(scroll, new Coord(0, 0));
		return scroll.cont;
	}

    public OptWnd() {
	this(true);
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
	if((sender == this) && (msg == "close")) {
	    hide();
	} else {
	    super.wdgmsg(sender, msg, args);
	}
    }

    public void show() {
	chpanel(main);
	super.show();
    }
}
