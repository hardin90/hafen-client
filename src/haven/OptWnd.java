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
import java.util.List;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.prefs.BackingStoreException;
import java.util.stream.Collectors;


public class OptWnd extends Window {
	private static final Text.Foundry sectionfndr = new Text.Foundry(Text.dfont.deriveFont(Font.BOLD, Text.cfg.label));
	public static final int VERTICAL_MARGIN = 10;
	public static final int HORIZONTAL_MARGIN = 5;
	public static final int VERTICAL_AUDIO_MARGIN = 5;
	public final Panel main, video, audio, display, map, general, combat, control, mapping, uis, quality, flowermenus, soundalarms, keybind, graphic;
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
	keybind = add(new Panel());
	graphic = add(new GraphicPanel(main));

	initMain(gopts);
	initAudio();
	initDisplay();
	initMinimap();
	initGeneral();
	initCombat();
	initControl();
	initUis();
	initQuality();
	initFlowermenus();
	initSoundAlarms();
	initKeyBind();
	initMapping();
	chpanel(main);
    }

	private void initMain(boolean gopts) {
		main.add(new PButton(200, "Graphic Settings", 'i', graphic), new Coord(0, 0));
		main.add(new PButton(200, "Video settings", 'v', video), new Coord(0, 30));
		main.add(new PButton(200, "Audio settings", 'a', audio), new Coord(0, 60));
		main.add(new PButton(200, "Display settings", 'd', display), new Coord(0, 90));
		main.add(new PButton(200, "Minimap settings", 'm', map), new Coord(0, 120));
		main.add(new PButton(200, "General settings", 'g', general), new Coord(210, 0));
		main.add(new PButton(200, "Combat settings", 'c', combat), new Coord(210, 30));
		main.add(new PButton(200, "Control settings", 'k', control), new Coord(210, 60));
		main.add(new PButton(200, "Mapping settings", 'e', mapping), new Coord(210, 90));
		main.add(new PButton(200, "UI settings", 'u', uis), new Coord(210, 120));
		main.add(new PButton(200, "Quality settings", 'q', quality), new Coord(420, 0));
		main.add(new PButton(200, "Menu settings", 'f', flowermenus), new Coord(420, 30));
		main.add(new PButton(200, "Sound alarms", 's', soundalarms), new Coord(420, 60));
		main.add(new PButton(200, "Key Bindings", 'b', keybind), new Coord(420, 90));
		if (gopts) {
			main.add(new Button(200, "Switch character") {
				public void click() {
					GameUI gui = gameui();
					gui.act("lo", "cs");
					// TODO: 14.08.2020 add cancel tasks
//					if (gui != null & gui.map != null)
//						gui.map.canceltasks();
				}
			}, new Coord(210, 300));
			main.add(new Button(200, "Log out") {
				public void click() {
					GameUI gui = gameui();
					gui.act("lo");
				 // TODO: 14.08.2020 add cancel tasks
//					if (gui != null & gui.map != null)
//						gui.map.canceltasks();
				}
			}, new Coord(210, 330));
		}
		main.add(new Button(200, "Close") {
			public void click() {
				OptWnd.this.hide();
			}
		}, new Coord(210, 360));
		main.pack();
	}

	public class GraphicPanel extends Panel {
		public GraphicPanel(Panel back) {
			super();
			add(new PButton(200, "Back", 27, back), new Coord(0, 310));
			pack();
		}

		public class CPanel extends Widget {
			public GSettings prefs;

			public CPanel(GSettings gprefs) {
				this.prefs = gprefs;
				int y = 0;
				add(new CheckBox("Render shadows") {
					{a = prefs.lshadow.val;}

					public void set(boolean val) {
						try {
							GSettings np = prefs.update(null, prefs.lshadow, val);
							ui.setgprefs(prefs = np);
						} catch(GSettings.SettingException e) {
							error(e.getMessage());
							return;
						}
						a = val;
					}
				}, new Coord(0, y));
				y += 20;
				add(new Label("Render scale"), new Coord(0, y));
				{
					Label dpy = add(new Label(""), new Coord(165, y + 15));
					final int steps = 4;
					add(new HSlider(160, -2 * steps, 2 * steps, (int)Math.round(steps * Math.log(prefs.rscale.val) / Math.log(2.0f))) {
						protected void added() {
							dpy();
							this.c.y = dpy.c.y + ((dpy.sz.y - this.sz.y) / 2);
						}
						void dpy() {
							dpy.settext(String.format("%.2f\u00d7", Math.pow(2, this.val / (double)steps)));
						}
						public void changed() {
							try {
								float val = (float)Math.pow(2, this.val / (double)steps);
								ui.setgprefs(prefs = prefs.update(null, prefs.rscale, val));
							} catch(GSettings.SettingException e) {
								error(e.getMessage());
								return;
							}
							dpy();
						}
					}, new Coord(0, y + 15));
				}
				y += 45;
				add(new CheckBox("Vertical sync") {
					{a = prefs.vsync.val;}

					public void set(boolean val) {
						try {
							GSettings np = prefs.update(null, prefs.vsync, val);
							ui.setgprefs(prefs = np);
						} catch(GSettings.SettingException e) {
							error(e.getMessage());
							return;
						}
						a = val;
					}
				}, new Coord(0, y));
				y += 20;
				add(new Label("Framerate limit (active window)"), new Coord(0, y));
				{
					Label dpy = add(new Label(""), new Coord(165, y + 15));
					final int max = 250;
					add(new HSlider(160, 1, max, (prefs.hz.val == Float.POSITIVE_INFINITY) ? max : prefs.hz.val.intValue()) {
						protected void added() {
							dpy();
							this.c.y = dpy.c.y + ((dpy.sz.y - this.sz.y) / 2);
						}
						void dpy() {
							if(this.val == max)
								dpy.settext("None");
							else
								dpy.settext(Integer.toString(this.val));
						}
						public void changed() {
							try {
								if(this.val > 10)
									this.val = (this.val / 2) * 2;
								float val = (this.val == max) ? Float.POSITIVE_INFINITY : this.val;
								ui.setgprefs(prefs = prefs.update(null, prefs.hz, val));
							} catch(GSettings.SettingException e) {
								error(e.getMessage());
								return;
							}
							dpy();
						}
					}, new Coord(0, y + 15));
				}
				y += 35;
				add(new Label("Framerate limit (background window)"), new Coord(0, y));
				{
					Label dpy = add(new Label(""), new Coord(165, y + 15));
					final int max = 250;
					add(new HSlider(160, 1, max, (prefs.bghz.val == Float.POSITIVE_INFINITY) ? max : prefs.bghz.val.intValue()) {
						protected void added() {
							dpy();
							this.c.y = dpy.c.y + ((dpy.sz.y - this.sz.y) / 2);
						}
						void dpy() {
							if(this.val == max)
								dpy.settext("None");
							else
								dpy.settext(Integer.toString(this.val));
						}
						public void changed() {
							try {
								if(this.val > 10)
									this.val = (this.val / 2) * 2;
								float val = (this.val == max) ? Float.POSITIVE_INFINITY : this.val;
								ui.setgprefs(prefs = prefs.update(null, prefs.bghz, val));
							} catch(GSettings.SettingException e) {
								error(e.getMessage());
								return;
							}
							dpy();
						}
					}, new Coord(0, y + 15));
				}
				y += 35;
				add(new Label("Frame sync mode"), new Coord(0, y));
				y += 15;
				{
					boolean[] done = {false};
					RadioGroup grp = new RadioGroup(this) {
						public void changed(int btn, String lbl) {
							if(!done[0])
								return;
							try {
								ui.setgprefs(prefs = prefs.update(null, prefs.syncmode, JOGLPanel.SyncMode.values()[btn]));
							} catch(GSettings.SettingException e) {
								error(e.getMessage());
								return;
							}
						}
					};
					Widget prev;
					prev = add(new Label("\u2191 Better performance, worse latency"), new Coord(5, y));
					y += prev.sz.y + 2;
					prev = grp.add("One-frame overlap", new Coord(5, y));
					y += prev.sz.y + 2;
					prev = grp.add("Tick overlap", new Coord(5, y));
					y += prev.sz.y + 2;
					prev = grp.add("CPU-sequential", new Coord(5, y));
					y += prev.sz.y + 2;
					prev = grp.add("GPU-sequential", new Coord(5, y));
					y += prev.sz.y + 2;
					prev = add(new Label("\u2193 Worse performance, better latency"), new Coord(5, y));
					y += prev.sz.y + 2;
					grp.check(prefs.syncmode.val.ordinal());
					done[0] = true;
				}
		/* XXXRENDER
		add(new CheckBox("Antialiasing") {
			{a = cf.fsaa.val;}
			public void set(boolean val) {
			    try {
				cf.fsaa.set(val);
			    } catch(GLSettings.SettingException e) {
				error(e.getMessage());
				return;
			    }
			    a = val;
			    cf.dirty = true;
			}
		    }, new Coord(0, y));
		y += 25;
		add(new Label("Anisotropic filtering"), new Coord(0, y));
		if(cf.anisotex.max() <= 1) {
		    add(new Label("(Not supported)"), new Coord(15, y + 15));
		} else {
		    final Label dpy = add(new Label(""), new Coord(165, y + 15));
		    add(new HSlider(160, (int)(cf.anisotex.min() * 2), (int)(cf.anisotex.max() * 2), (int)(cf.anisotex.val * 2)) {
			    protected void added() {
				dpy();
				this.c.y = dpy.c.y + ((dpy.sz.y - this.sz.y) / 2);
			    }
			    void dpy() {
				if(val < 2)
				    dpy.settext("Off");
				else
				    dpy.settext(String.format("%.1f\u00d7", (val / 2.0)));
			    }
			    public void changed() {
				try {
				    cf.anisotex.set(val / 2.0f);
				} catch(GLSettings.SettingException e) {
				    error(e.getMessage());
				    return;
				}
				dpy();
				cf.dirty = true;
			    }
			}, new Coord(0, y + 15));
		}
		*/
				add(new Button(200, "Reset to defaults") {
					public void click() {
						ui.setgprefs(GSettings.defaults());
						curcf.destroy();
						curcf = null;
					}
				}, new Coord(0, 280));
				pack();
			}
		}

		private CPanel curcf = null;
		public void draw(GOut g) {
			if((curcf == null) || (ui.gprefs != curcf.prefs)) {
				if(curcf != null)
					curcf.destroy();
				curcf = add(new CPanel(ui.gprefs), Coord.z);
			}
			super.draw(g);
		}
	}

	private void initAudio() {
		initAudioFirstColumn();
		audio.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
		audio.pack();
	}

	private void initAudioFirstColumn() {
		final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(audio, new Coord(620, 350)));
		appender.setVerticalMargin(0);
		appender.add(new Label("Master audio volume"));
		appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
		appender.add(new HSlider(200, 0, 1000, (int) (Audio.volume * 1000)) {
			public void changed() {
				Audio.setvolume(val / 1000.0);
			}
		});
		appender.setVerticalMargin(0);
		appender.add(new Label("In-game event volume"));
		appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
		appender.add(new HSlider(200, 0, 1000, 0) {
			protected void attach(UI ui) {
				super.attach(ui);
				val = (int) (ui.audio.pos.volume * 1000);
			}

			public void changed() {
				ui.audio.pos.setvolume(val / 1000.0);
			}
		});
		appender.setVerticalMargin(0);
		appender.add(new Label("Ambient volume"));
		appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
		appender.add(new HSlider(200, 0, 1000, 0) {
			protected void attach(UI ui) {
				super.attach(ui);
				val = (int) (ui.audio.amb.volume * 1000);
			}

			public void changed() {
				ui.audio.amb.setvolume(val / 1000.0);
			}
		});
		appender.setVerticalMargin(0);
		appender.add(new Label("Timers alarm volume"));
		appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
		appender.add(new HSlider(200, 0, 1000, 0) {
			protected void attach(UI ui) {
				super.attach(ui);
				val = (int) (Config.timersalarmvol * 1000);
			}

			public void changed() {
				double vol = val / 1000.0;
				Config.timersalarmvol = vol;
				Utils.setprefd("timersalarmvol", vol);
			}
		});
		appender.setVerticalMargin(0);
		appender.add(new Label("'Chip' sound volume"));
		appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
		appender.add(new HSlider(200, 0, 1000, 0) {
			protected void attach(UI ui) {
				super.attach(ui);
				val = (int) (Config.sfxchipvol * 1000);
			}

			public void changed() {
				double vol = val / 1000.0;
				Config.sfxchipvol = vol;
				Utils.setprefd("sfxchipvol", vol);
			}
		});
		appender.setVerticalMargin(0);
		appender.add(new Label("Quern sound volume"));
		appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
		appender.add(new HSlider(200, 0, 1000, 0) {
			protected void attach(UI ui) {
				super.attach(ui);
				val = (int) (Config.sfxquernvol * 1000);
			}

			public void changed() {
				double vol = val / 1000.0;
				Config.sfxquernvol = vol;
				Utils.setprefd("sfxquernvol", vol);
			}
		});
		appender.setVerticalMargin(0);
		appender.add(new Label("'Whip' sound volume"));
		appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
		appender.add(new HSlider(200, 0, 1000, 0) {
			protected void attach(UI ui) {
				super.attach(ui);
				val = (int) (Config.sfxwhipvol * 1000);
			}

			public void changed() {
				double vol = val / 1000.0;
				Config.sfxwhipvol = vol;
				Utils.setprefd("sfxwhipvol", vol);
			}
		});
		appender.setVerticalMargin(0);
		appender.add(new Label("Fireplace sound volume (req. restart)"));
		appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
		appender.add(new HSlider(200, 0, 1000, 0) {
			protected void attach(UI ui) {
				super.attach(ui);
				val = (int) (Config.sfxfirevol * 1000);
			}

			public void changed() {
				double vol = val / 1000.0;
				Config.sfxfirevol = vol;
				Utils.setprefd("sfxfirevol", vol);
			}
		});
		appender.setVerticalMargin(0);
		appender.add(new Label("Bees sound volume (req. logout)"));
		appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
		appender.add(new HSlider(200, 0, 1000, 0) {
			protected void attach(UI ui) {
				super.attach(ui);
				val = (int) (Config.sfxbeevol * 1000);
			}

			public void changed() {
				double vol = val / 1000.0;
				Config.sfxbeevol = vol;
				Utils.setprefd("sfxbeevol", vol);
			}
		});
	}

	private void initDisplay() {
		initDisplayFirstColumn();
		display.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
		display.pack();
	}

	private void initDisplayFirstColumn() {
		final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(display, new Coord(620, 350)));
		appender.setVerticalMargin(VERTICAL_MARGIN);
		appender.add(new CheckBox("Display kin names") {
			{
				a = Config.showkinnames;
			}

			public void set(boolean val) {
				Utils.setprefb("showkinnames", val);
				Config.showkinnames = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Display item completion progress bar") {
			{
				a = Config.itemmeterbar;
			}

			public void set(boolean val) {
				Utils.setprefb("itemmeterbar", val);
				Config.itemmeterbar = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Show hourglass percentage") {
			{
				a = Config.showprogressperc;
			}

			public void set(boolean val) {
				Utils.setprefb("showprogressperc", val);
				Config.showprogressperc = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Show attributes & softcap values in craft window") {
			{
				a = Config.showcraftcap;
			}

			public void set(boolean val) {
				Utils.setprefb("showcraftcap", val);
				Config.showcraftcap = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Show objects health") {
			{
				a = Config.showgobhp;
			}

			public void set(boolean val) {
				Utils.setprefb("showgobhp", val);
				Config.showgobhp = val;
				a = val;

				GameUI gui = gameui();
				// TODO: 14.08.2020 resolve how make sprite
//				if (gui != null && gui.map != null) {
//					if (val)
//						gui.map.addHealthSprites();
//					else
//						gui.map.removeCustomSprites(Sprite.GOB_HEALTH_ID);
//				}
			}
		});
		appender.add(new CheckBox("Show player's path") {
			{
				a = Config.showplayerpaths;
			}

			public void set(boolean val) {
				Utils.setprefb("showplayerpaths", val);
				Config.showplayerpaths = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Show animal radius") {
			{
				a = Config.showanimalrad;
			}

			public void set(boolean val) {
				Utils.setprefb("showanimalrad", val);
				Config.showanimalrad = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Highlight empty/finished drying frames") {
			{
				a = Config.showdframestatus;
			}

			public void set(boolean val) {
				Utils.setprefb("showdframestatus", val);
				Config.showdframestatus = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Highlight finished garden pots") {
			{
				a = Config.highlightpots;
			}

			public void set(boolean val) {
				Utils.setprefb("highlightpots", val);
				Config.highlightpots = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Draw circles around party members") {
			{
				a = Config.partycircles;
			}

			public void set(boolean val) {
				Utils.setprefb("partycircles", val);
				Config.partycircles = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Show last used curios in study window") {
			{
				a = Config.studyhist;
			}

			public void set(boolean val) {
				Utils.setprefb("studyhist", val);
				Config.studyhist = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Display buff icon when study has free slots") {
			{
				a = Config.studybuff;
			}

			public void set(boolean val) {
				Utils.setprefb("studybuff", val);
				Config.studybuff = val;
				a = val;
			}
		});
		appender.add(new CheckBox("Miniature trees (req. logout)") {
			{
				a = Config.bonsai;
			}

			public void set(boolean val) {
				Utils.setprefb("bonsai", val);
				Config.bonsai = val;
				a = val;
			}
		});
	}

    private void initMinimap() {
        map.add(new Label("Show boulders:"), new Coord(10, 0));
        map.add(new Label("Show bushes:"), new Coord(165, 0));
        map.add(new Label("Show trees:"), new Coord(320, 0));
        map.add(new Label("Hide icons:"), new Coord(475, 0));

        map.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
        map.pack();
    }

    private void initGeneral() {
        final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(general, new Coord(620, 350)));

        appender.setVerticalMargin(VERTICAL_MARGIN);
        appender.setHorizontalMargin(HORIZONTAL_MARGIN);

        appender.add(new CheckBox("Save chat logs to disk") {
            {
                a = Config.chatsave;
            }

            public void set(boolean val) {
                Utils.setprefb("chatsave", val);
                Config.chatsave = val;
                a = val;
                if (!val && Config.chatlog != null) {
                    try {
                        Config.chatlog.close();
                        Config.chatlog = null;
                    } catch (Exception e) {
                    }
                }
            }
        });
        appender.add(new CheckBox("Notify when kin comes online") {
            {
                a = Config.notifykinonline;
            }

            public void set(boolean val) {
                Utils.setprefb("notifykinonline", val);
                Config.notifykinonline = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Auto hearth") {
            {
                a = Config.autohearth;
            }

            public void set(boolean val) {
                Utils.setprefb("autohearth", val);
                Config.autohearth = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Auto logout on unknown/red players") {
            {
                a = Config.autologout;
            }

            public void set(boolean val) {
                Utils.setprefb("autologout", val);
                Config.autologout = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Run on login") {
            {
                a = Config.runonlogin;
            }

            public void set(boolean val) {
                Utils.setprefb("runonlogin", val);
                Config.runonlogin = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Show server time") {
            {
                a = Config.showservertime;
            }

            public void set(boolean val) {
                Utils.setprefb("showservertime", val);
                Config.showservertime = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Enable tracking on login") {
            {
                a = Config.enabletracking;
            }

            public void set(boolean val) {
                Utils.setprefb("enabletracking", val);
                Config.enabletracking = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Enable criminal acts on login") {
            {
                a = Config.enablecrime;
            }

            public void set(boolean val) {
                Utils.setprefb("enablecrime", val);
                Config.enablecrime = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Drop mined stones") {
            {
                a = Config.dropMinedStones;
            }

            public void set(boolean val) {
                Utils.setprefb("dropMinedStones", val);
                Config.dropMinedStones = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Drop mined ore") {
            {
                a = Config.dropMinedOre;
            }

            public void set(boolean val) {
                Utils.setprefb("dropMinedOre", val);
                Config.dropMinedOre = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Drop mined silver/gold ore") {
            {
                a = Config.dropMinedOrePrecious;
            }

            public void set(boolean val) {
                Utils.setprefb("dropMinedOrePrecious", val);
                Config.dropMinedOrePrecious = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Drop mined cat gold, petrified seashells, strange crystals") {
            {
                a = Config.dropMinedCurios;
            }

            public void set(boolean val) {
                Utils.setprefb("dropMinedCurios", val);
                Config.dropMinedCurios = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Drop everything!!!") {
            {
                a = Config.dropEverything;
            }

            public void set(boolean val) {
                //Utils.setprefb("dropEverything", val);
                Config.dropEverything = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Drop soil") {
            {
                a = Config.dropSoil;
            }

            public void set(boolean val) {
                Utils.setprefb("dropSoil", val);
                Config.dropSoil = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Send food details to the food service (https://food.havenandhearth.link)") {
            {
                a = Config.foodService;
            }

            public void set(boolean val) {
                Utils.setprefb("foodService", val);
                Config.foodService = val;
                a = val;
            }
        });
        general.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
        general.pack();
    }

    private void initCombat() {
        final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(combat, new Coord(620, 350)));

        appender.setVerticalMargin(VERTICAL_MARGIN);
        appender.setHorizontalMargin(HORIZONTAL_MARGIN);

        appender.add(new CheckBox("Display damage") {
            {
                a = Config.showdmgop;
            }

            public void set(boolean val) {
                Utils.setprefb("showdmgop", val);
                Config.showdmgop = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Highlight current opponent") {
            {
                a = Config.hlightcuropp;
            }

            public void set(boolean val) {
                Utils.setprefb("hlightcuropp", val);
                Config.hlightcuropp = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Display cooldown time") {
            {
                a = Config.showcooldown;
            }

            public void set(boolean val) {
                Utils.setprefb("showcooldown", val);
                Config.showcooldown = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Show arrow vectors") {
            {
                a = Config.showarchvector;
            }

            public void set(boolean val) {
                Utils.setprefb("showarchvector", val);
                Config.showarchvector = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Log combat actions to system log") {
            {
                a = Config.logcombatactions;
            }

            public void set(boolean val) {
                Utils.setprefb("logcombatactions", val);
                Config.logcombatactions = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Alternative combat UI") {
            {
                a = Config.altfightui;
            }

            public void set(boolean val) {
                Utils.setprefb("altfightui", val);
                Config.altfightui = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Simplified opening indicators") {
            {
                a = Config.combaltopenings;
            }

            public void set(boolean val) {
                Utils.setprefb("combaltopenings", val);
                Config.combaltopenings = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Show key bindings in combat UI") {
            {
                a = Config.combshowkeys;
            }

            public void set(boolean val) {
                Utils.setprefb("combshowkeys", val);
                Config.combshowkeys = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Aggro players in proximity to the mouse cursor") {
            {
                a = Config.proximityaggro;
            }

            public void set(boolean val) {
                Utils.setprefb("proximityaggro", val);
                Config.proximityaggro = val;
                a = val;
            }
        });

        combat.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
        combat.pack();
    }

    private void initControl() {
        final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(control, new Coord(620, 350)));

        appender.setVerticalMargin(VERTICAL_MARGIN);
        appender.setHorizontalMargin(HORIZONTAL_MARGIN);

        appender.addRow(new Label("Bad camera scrolling sensitivity"),
                new HSlider(50, 0, 50, 0) {
                    protected void attach(UI ui) {
                        super.attach(ui);
                        val = Config.badcamsensitivity;
                    }

                    public void changed() {
                        Config.badcamsensitivity = val;
                        Utils.setprefi("badcamsensitivity", val);
                    }
                });
        appender.add(new CheckBox("Use French (AZERTY) keyboard layout") {
            {
                a = Config.userazerty;
            }

            public void set(boolean val) {
                Utils.setprefb("userazerty", val);
                Config.userazerty = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Reverse bad camera MMB x-axis") {
            {
                a = Config.reversebadcamx;
            }

            public void set(boolean val) {
                Utils.setprefb("reversebadcamx", val);
                Config.reversebadcamx = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Reverse bad camera MMB y-axis") {
            {
                a = Config.reversebadcamy;
            }

            public void set(boolean val) {
                Utils.setprefb("reversebadcamy", val);
                Config.reversebadcamy = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Force hardware cursor (req. restart)") {
            {
                a = Config.hwcursor;
            }

            public void set(boolean val) {
                Utils.setprefb("hwcursor", val);
                Config.hwcursor = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Disable dropping items over water (overridable with Ctrl)") {
            {
                a = Config.nodropping;
            }

            public void set(boolean val) {
                Utils.setprefb("nodropping", val);
                Config.nodropping = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Disable dropping items over anywhere (overridable with Ctrl)") {
            {
                a = Config.nodropping_all;
            }

            public void set(boolean val) {
                Utils.setprefb("nodropping_all", val);
                Config.nodropping_all = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Enable full zoom-out in Ortho cam") {
            {
                a = Config.enableorthofullzoom;
            }

            public void set(boolean val) {
                Utils.setprefb("enableorthofullzoom", val);
                Config.enableorthofullzoom = val;
                a = val;
            }
        });

        control.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
        control.pack();
    }

    private void initUis() {
        final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(uis, new Coord(620, 310)));

        appender.setVerticalMargin(VERTICAL_MARGIN);
        appender.setHorizontalMargin(HORIZONTAL_MARGIN);

        appender.addRow(new Label("Language (req. restart):"), langDropdown());
//        appender.add(new CheckBox("Show quick hand slots") {
//            {
//                a = Config.quickslots;
//            }
//
//            public void set(boolean val) {
//                Utils.setprefb("quickslots", val);
//                Config.quickslots = val;
//                a = val;
//
//                try {
//                    Widget qs = ((GameUI) parent.parent.parent).quickslots;
//                    if (qs != null) {
//                        if (val)
//                            qs.show();
//                        else
//                            qs.hide();
//                    }
//                } catch (ClassCastException e) { // in case we are at the login screen
//                }
//            }
//        });
        appender.add(new CheckBox("Alternative equipment belt window") {
            {
                a = Config.quickbelt;
            }

            public void set(boolean val) {
                Utils.setprefb("quickbelt", val);
                Config.quickbelt = val;
                a = val;
            }
        });
//        appender.add(new CheckBox("Show F-key toolbar") {
//            {
//                a = Config.fbelt;
//            }
//
//            public void set(boolean val) {
//                Utils.setprefb("fbelt", val);
//                Config.fbelt = val;
//                a = val;
//                GameUI gui = gameui();
//                if (gui != null) {
//                    FBelt fbelt = gui.fbelt;
//                    if (fbelt != null) {
//                        if (val)
//                            fbelt.show();
//                        else
//                            fbelt.hide();
//                    }
//                }
//            }
//        });
        appender.add(new CheckBox("Show inventory on login") {
            {
                a = Config.showinvonlogin;
            }

            public void set(boolean val) {
                Utils.setprefb("showinvonlogin", val);
                Config.showinvonlogin = val;
                a = val;
            }
        });
//        appender.add(new CheckBox("Show Craft/Build history toolbar") {
//            {
//                a = Config.histbelt;
//            }
//
//            public void set(boolean val) {
//                Utils.setprefb("histbelt", val);
//                Config.histbelt = val;
//                a = val;
//                GameUI gui = gameui();
//                if (gui != null) {
//                    CraftHistoryBelt histbelt = gui.histbelt;
//                    if (histbelt != null) {
//                        if (val)
//                            histbelt.show();
//                        else
//                            histbelt.hide();
//                    }
//                }
//            }
//        });
        appender.add(new CheckBox("Display confirmation dialog when using magic") {
            {
                a = Config.confirmmagic;
            }

            public void set(boolean val) {
                Utils.setprefb("confirmmagic", val);
                Config.confirmmagic = val;
                a = val;
            }
        });
//        appender.addRow(new Label("Tree bounding box color (6-digit HEX):"),
//                new TextEntry(85, Config.treeboxclr) {
//                    @Override
//                    public boolean keydown(KeyEvent ev) {
//                        if (!parent.visible)
//                            return false;
//
//                        boolean ret = buf.key(ev);
//                        if (text.length() == 6) {
//                            Color clr = Utils.hex2rgb(text);
//                            if (clr != null) {
//                                GobHitbox.fillclrstate = new States.ColState(clr);
//                                Utils.setpref("treeboxclr", text);
//                            }
//                        }
//                        return ret;
//                    }
//                }
//        );
//        appender.addRow(new Label("Chat font size (req. restart):"), makeFontSizeChatDropdown());
        appender.add(new CheckBox("Font antialiasing") {
            {
                a = Config.fontaa;
            }

            public void set(boolean val) {
                Utils.setprefb("fontaa", val);
                Config.fontaa = val;
                a = val;
            }
        });
        appender.addRow(new CheckBox("Custom interface font (req. restart):") {
            {
                a = Config.usefont;
            }

            public void set(boolean val) {
                Utils.setprefb("usefont", val);
                Config.usefont = val;
                a = val;
            }
        }, makeFontsDropdown());

        final Label fontAdd = new Label("");
        appender.addRow(
                new Label("Increase font size by (req. restart):"),
                new HSlider(160, 0, 3, Config.fontadd) {
                    public void added() {
                        updateLabel();
                    }
                    public void changed() {
                        Utils.setprefi("fontadd", val);
                        Config.fontadd = val;
                        updateLabel();
                    }
                    private void updateLabel() {
                        fontAdd.settext(String.format("%d", val));
                    }
                },
                fontAdd
        );

        Button resetWndBtn = new Button(220, "Reset Windows (req. logout)") {
//            @Override
//            public void click() {
//                try {
//                    for (String key : Utils.prefs().keys()) {
//                        if (key.endsWith("_c")) {
//                            Utils.delpref(key);
//                        }
//                    }
//                } catch (BackingStoreException e) {
//                }
//                Utils.delpref("mmapc");
//                Utils.delpref("mmapwndsz");
//                Utils.delpref("mmapsz");
//                Utils.delpref("quickslotsc");
//                Utils.delpref("chatsz");
//                Utils.delpref("chatvis");
//                Utils.delpref("menu-visible");
//                Utils.delpref("fbelt_vertical");
//            }
        };
        uis.add(resetWndBtn, new Coord(620 / 2 - resetWndBtn.sz.x / 2 , 320));
        uis.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
        uis.pack();
    }

    private void initQuality() {
        final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(quality, new Coord(620, 350)));
        appender.setVerticalMargin(VERTICAL_MARGIN);
        appender.setHorizontalMargin(HORIZONTAL_MARGIN);
        appender.add(new CheckBox("Show item quality") {
            {
                a = Config.showquality;
            }

            public void set(boolean val) {
                Utils.setprefb("showquality", val);
                Config.showquality = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Round item quality to a whole number") {
            {
                a = Config.qualitywhole;
            }

            public void set(boolean val) {
                Utils.setprefb("qualitywhole", val);
                Config.qualitywhole = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Draw background for quality values") {
            {
                a = Config.qualitybg;
            }

            public void set(boolean val) {
                Utils.setprefb("qualitybg", val);
                Config.qualitybg = val;
                a = val;
            }
        });
        appender.addRow(
                new Label("Background transparency (req. restart):"),
                new HSlider(200, 0, 255, Config.qualitybgtransparency) {
                    public void changed() {
                        Utils.setprefi("qualitybgtransparency", val);
                        Config.qualitybgtransparency = val;
                    }
                });

        quality.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
        quality.pack();
    }

    private void initFlowermenus() {
        final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(flowermenus, new Coord(620, 350)));

        appender.setVerticalMargin(VERTICAL_MARGIN);
        appender.setHorizontalMargin(HORIZONTAL_MARGIN);

        appender.add(new CheckBox("Automatically pick all clustered mussels (auto 'Pick' needs to be enabled)") {
            {
                a = Config.autopickmussels;
            }

            public void set(boolean val) {
                Utils.setprefb("autopickmussels", val);
                Config.autopickmussels = val;
                a = val;
            }
        });
        appender.add(new Label("Automatic selecton:"));

        CheckListbox flowerlist = new CheckListbox(140, 17) {
            @Override
            protected void itemclick(CheckListboxItem itm, int button) {
                super.itemclick(itm, button);
                Utils.setprefchklst("flowersel", Config.flowermenus);
            }
        };

        Utils.loadprefchklist("flowersel", Config.flowermenus);
        for (CheckListboxItem itm : Config.flowermenus.values())
            flowerlist.items.add(itm);
        flowermenus.add(flowerlist, new Coord(0, 50));

        flowermenus.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
        flowermenus.pack();
    }

    private void initSoundAlarms() {
        final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(soundalarms, new Coord(620, 350)));

        appender.setVerticalMargin(VERTICAL_MARGIN);
        appender.setHorizontalMargin(HORIZONTAL_MARGIN);

        appender.setVerticalMargin(0);
        appender.add(new CheckBox("Alarm on unknown players") {
            {
                a = Config.alarmunknown;
            }

            public void set(boolean val) {
                Utils.setprefb("alarmunknown", val);
                Config.alarmunknown = val;
                a = val;
            }
        });
        appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
        appender.add(new HSlider(200, 0, 1000, 0) {
            protected void attach(UI ui) {
                super.attach(ui);
                val = (int)(Config.alarmunknownvol * 1000);
            }

            public void changed() {
                double vol = val / 1000.0;
                Config.alarmunknownvol = vol;
                Utils.setprefd("alarmunknownvol", vol);
            }
        });
        appender.setVerticalMargin(0);
        appender.add(new CheckBox("Alarm on red players") {
            {
                a = Config.alarmred;
            }

            public void set(boolean val) {
                Utils.setprefb("alarmred", val);
                Config.alarmred = val;
                a = val;
            }
        });
        appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
        appender.add(new HSlider(200, 0, 1000, 0) {
            protected void attach(UI ui) {
                super.attach(ui);
                val = (int) (Config.alarmredvol * 1000);
            }

            public void changed() {
                double vol = val / 1000.0;
                Config.alarmredvol = vol;
                Utils.setprefd("alarmredvol", vol);
            }
        });
        appender.setVerticalMargin(0);
        appender.add(new CheckBox("Alarm on new private/party chat") {
            {
                a = Config.chatalarm;
            }

            public void set(boolean val) {
                Utils.setprefb("chatalarm", val);
                Config.chatalarm = val;
                a = val;
            }
        });
        appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
        appender.add(new HSlider(200, 0, 1000, 0) {
            protected void attach(UI ui) {
                super.attach(ui);
                val = (int) (Config.chatalarmvol * 1000);
            }

            public void changed() {
                double vol = val / 1000.0;
                Config.chatalarmvol = vol;
                Utils.setprefd("chatalarmvol", vol);
            }
        });
        appender.setVerticalMargin(0);
        appender.add(new CheckBox("Alarm when curio finishes") {
            {
                a = Config.studyalarm;
            }

            public void set(boolean val) {
                Utils.setprefb("studyalarm", val);
                Config.studyalarm = val;
                a = val;
            }
        });
        appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
        appender.add(new HSlider(200, 0, 1000, 0) {
            protected void attach(UI ui) {
                super.attach(ui);
                val = (int) (Config.studyalarmvol * 1000);
            }

            public void changed() {
                double vol = val / 1000.0;
                Config.studyalarmvol = vol;
                Utils.setprefd("studyalarmvol", vol);
            }
        });
        appender.add(new CheckBox("Alarm on trolls") {
            {
                a = Config.alarmtroll;
            }

            public void set(boolean val) {
                Utils.setprefb("alarmtroll", val);
                Config.alarmtroll = val;
                a = val;
            }
        });
        appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
        appender.add(new HSlider(200, 0, 1000, 0) {
            protected void attach(UI ui) {
                super.attach(ui);
                val = (int) (Config.alarmtrollvol * 1000);
            }

            public void changed() {
                double vol = val / 1000.0;
                Config.alarmtrollvol = vol;
                Utils.setprefd("alarmtrollvol", vol);
            }
        });
        appender.setVerticalMargin(0);
        appender.add(new CheckBox("Alarm on battering rams and catapults") {
            {
                a = Config.alarmbram;
            }

            public void set(boolean val) {
                Utils.setprefb("alarmbram", val);
                Config.alarmbram = val;
                a = val;
            }
        });
        appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
        appender.add(new HSlider(200, 0, 1000, 0) {
            protected void attach(UI ui) {
                super.attach(ui);
                val = (int) (Config.alarmbramvol * 1000);
            }

            public void changed() {
                double vol = val / 1000.0;
                Config.alarmbramvol = vol;
                Utils.setprefd("alarmbramvol", vol);
            }
        });
        appender.setVerticalMargin(0);
        appender.add(new CheckBox("Alarm on localized resources") {
            {
                a = Config.alarmlocres;
            }

            public void set(boolean val) {
                Utils.setprefb("alarmlocres", val);
                Config.alarmlocres = val;
                a = val;
            }
        });
        appender.setVerticalMargin(VERTICAL_AUDIO_MARGIN);
        appender.add(new HSlider(200, 0, 1000, 0) {
            protected void attach(UI ui) {
                super.attach(ui);
                val = (int) (Config.alarmlocresvol * 1000);
            }

            public void changed() {
                double vol = val / 1000.0;
                Config.alarmlocresvol = vol;
                Utils.setprefd("alarmlocresvol", vol);
            }
        });

        soundalarms.add(new Label("Alarm on"), new Coord(470, 0));
        CheckListbox itemslist = new CheckListbox(145, 18) {
            @Override
            protected void itemclick(CheckListboxItem itm, int button) {
                super.itemclick(itm, button);
                Utils.setprefchklst("alarmitems", Config.alarmitems);
            }
        };
        for (CheckListboxItem itm : Config.alarmitems.values())
            itemslist.items.add(itm);
        soundalarms.add(itemslist, new Coord(470, 15));
        soundalarms.add(new HSlider(145, 0, 1000, 0) {
            protected void attach(UI ui) {
                super.attach(ui);
                val = (int) (Config.alarmonforagablesvol * 1000);
            }

            public void changed() {
                double vol = val / 1000.0;
                Config.alarmonforagablesvol = vol;
                Utils.setprefd("alarmonforagablesvol", vol);
            }
        }, new Coord(470, 340));

        soundalarms.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
        soundalarms.pack();
    }

    private void initMapping() {
        final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(mapping, new Coord(620, 350)));

        appender.setVerticalMargin(VERTICAL_MARGIN);
        appender.setHorizontalMargin(HORIZONTAL_MARGIN);

        appender.add(new Label("Online Auto-Mapper Service:", sectionfndr));

        appender.addRow(new Label("Mapping server URL (req. restart):"),
                new TextEntry(240, Config.mapperUrl) {
                    @Override
                    public boolean keydown(KeyEvent ev) {
                        if (!parent.visible)
                            return false;
                        Utils.setpref("mapperUrl", text);
                        return buf.key(ev);
                    }
                }
        );
        appender.add(new CheckBox("Enable mapping service") {
            {
                a = Config.mapperEnabled;
            }

            public void set(boolean val) {
                Utils.setprefb("mapperEnabled", val);
                Config.mapperEnabled = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Hide character name") {
            {
                a = Config.mapperHashName;
            }

            public void set(boolean val) {
                Utils.setprefb("mapperHashName", val);
                Config.mapperHashName = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Enable navigation tracking") {
            {
                a = Config.enableNavigationTracking;
            }

            public void set(boolean val) {
                Utils.setprefb("enableNavigationTracking", val);
                Config.enableNavigationTracking = val;
                a = val;
            }
        });
        appender.add(new CheckBox("Upload custom GREEN markers to map") {
            {
                a = Config.sendCustomMarkers;
            }

            public void set(boolean val) {
                Utils.setprefb("sendCustomMarkers", val);
                Config.sendCustomMarkers = val;
                a = val;
            }
        });

        appender.add(new Label(""));
        appender.add(new Label("Vendan Map-v4:", sectionfndr));

        appender.addRow(new Label("Server URL:"),
                new TextEntry(240, Utils.getpref("vendan-mapv4-endpoint", "")) {
                    @Override
                    public boolean keydown(KeyEvent ev) {
                        if (!parent.visible)
                            return false;
                        Utils.setpref("vendan-mapv4-endpoint", text);
                        //MappingClient.getInstance().SetEndpoint(text);
                        return buf.key(ev);
                    }
                }
        );

        appender.add(new CheckBox("Enable mapv4 mapper") {
            {
                a = Config.vendanMapv4;
            }

            public void set(boolean val) {
                Utils.setprefb("vendan-mapv4", val);
                Config.vendanMapv4 = val;
                //MappingClient.getInstance().EnableGridUploads(Config.vendanMapv4);
                //MappingClient.getInstance().EnableTracking(Config.vendanMapv4);
                a = val;
            }
        });

        appender.add(new CheckBox("Upload custom GREEN markers to map") {
            {
                a = Config.vendanGreenMarkers;
            }

            public void set(boolean val) {
                Utils.setprefb("vendan-mapv4-green-markers", val);
                Config.vendanGreenMarkers = val;
                a = val;
            }
        });

        appender.add(new Label(""));
        appender.add(new Label("Locally saved map tiles for 3rd party tools:", sectionfndr));

        appender.add(new CheckBox("Save map tiles to disk") {
            {
                a = Config.savemmap;
            }

            public void set(boolean val) {
                Utils.setprefb("savemmap", val);
                Config.savemmap = val;
                MapGridSave.mgs = null;
                a = val;
            }
        });

        mapping.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
        mapping.pack();
    }

	private final static int KB_NAME_W = 170;

    private void initKeyBind() {
        final WidgetVerticalAppender appender = new WidgetVerticalAppender(withScrollport(keybind, new Coord(620, 350)));

        appender.setVerticalMargin(VERTICAL_MARGIN);
        appender.setHorizontalMargin(HORIZONTAL_MARGIN);

        appender.addRow(KB_NAME_W, new Label("Inventory"), new SetButton(175, GameUI.kb_inv));
        appender.addRow(KB_NAME_W, new Label("Equipment"), new SetButton(175, GameUI.kb_equ));
        appender.addRow(KB_NAME_W, new Label("Character sheet"), new SetButton(175, GameUI.kb_chr));
        appender.addRow(KB_NAME_W, new Label("Kith & Kin"), new SetButton(175, GameUI.kb_bud));
        appender.addRow(KB_NAME_W, new Label("Options"), new SetButton(175, GameUI.kb_opt));
        appender.addRow(KB_NAME_W, new Label("Toggle chat"), new SetButton(175, GameUI.kb_chat));
        appender.addRow(KB_NAME_W, new Label("Quick chat"), new SetButton(175, ChatUI.kb_quick));
        appender.addRow(KB_NAME_W, new Label("Take screenshot & Upload "), new SetButton(175, GameUI.kb_shoot));
        appender.addRow(KB_NAME_W, new Label("Take screenshot & Save"), new SetButton(175, GameUI.kb_shoot_save));
        appender.addRow(KB_NAME_W, new Label("Combat action 1"), new SetButton(175, Fightsess.kb_acts[0]));
        for(int i = 1; i < Fightsess.kb_acts.length; i++) {
            appender.addRow(KB_NAME_W, new Label("Combat action " + (i + 1)), new SetButton(175, Fightsess.kb_acts[i]));
        }
        appender.addRow(KB_NAME_W, new Label("Switch combat opponent"), new SetButton(175, Fightsess.kb_switch));
        appender.addRow(KB_NAME_W, new Label("Drink hotkey"), new SetButton(175, GameUI.kb_drink));

        appender.addRow(KB_NAME_W, new Label("Bind other elements..."), new PointBind(200));

        keybind.add(new PButton(200, "Back", 27, main), new Coord(210, 360));
        keybind.pack();
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
			if (ev.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				cmd.set(null);
				super.set(cmd.key());
				return (true);
			}
			return (super.handle(ev));
		}

		public Object tooltip(Coord c, Widget prev) {
			return (kbtt.tex());
		}
	}

    private Dropbox<Locale> langDropdown() {
        List<Locale> languages = enumerateLanguages();
        List<String> values = languages.stream().map(x -> x.getDisplayName()).collect(Collectors.toList());
        return new Dropbox<Locale>(10, values) {
            {
                super.change(new Locale(Resource.language));
            }

            @Override
            protected Locale listitem(int i) {
                return languages.get(i);
            }

            @Override
            protected int listitems() {
                return languages.size();
            }

            @Override
            protected void drawitem(GOut g, Locale item, int i) {
                g.text(item.getDisplayName(), Coord.z);
            }

            @Override
            public void change(Locale item) {
                super.change(item);
                Utils.setpref("language", item.toString());
            }
        };
    }

	private List<Locale> enumerateLanguages() {
		Set<Locale> languages = new HashSet<>();
		languages.add(new Locale("en"));

		Enumeration<URL> en;
		try {
			en = this.getClass().getClassLoader().getResources("l10n");
			if (en.hasMoreElements()) {
				URL url = en.nextElement();
				JarURLConnection urlcon = (JarURLConnection) (url.openConnection());
				try (JarFile jar = urlcon.getJarFile()) {
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						String name = entries.nextElement().getName();
						// we assume that if tooltip localization exists then the rest exist as well
						// up to dev to make sure that it's true
						if (name.startsWith("l10n/" + Resource.BUNDLE_TOOLTIP))
							languages.add(new Locale(name.substring(13, 15)));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<Locale>(languages);
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

	@SuppressWarnings("unchecked")
	private Dropbox<String> makeFontsDropdown() {
		final List<String> fonts = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		return new Dropbox<String>(8, fonts) {
			{
				super.change(Config.font);
			}

			@Override
			protected String listitem(int i) {
				return fonts.get(i);
			}

			@Override
			protected int listitems() {
				return fonts.size();
			}

			@Override
			protected void drawitem(GOut g, String item, int i) {
				g.text(item, Coord.z);
			}

			@Override
			public void change(String item) {
				super.change(item);
				Config.font = item;
				Utils.setpref("font", item);
			}
		};
	}
}
