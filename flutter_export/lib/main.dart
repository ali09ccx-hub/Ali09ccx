import 'dart:async';
import 'dart:convert';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:intl/intl.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const VirtualPhoneApp());
}

class VirtualPhoneApp extends StatelessWidget {
  const VirtualPhoneApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Virtual Phone Simulator',
      debugShowCheckedModeBanner: false,
      themeMode: ThemeMode.dark,
      darkTheme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: const Color(0xFF090D16),
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFF0284C7),
          secondary: Color(0xFF38BDF8),
        ),
      ),
      home: const VirtualPhoneScreen(),
    );
  }
}

// ==================== Data Models ====================
class NoteItem {
  final String id;
  String title;
  String content;
  int timestamp;
  int colorHex;

  NoteItem({
    required this.id,
    required this.title,
    required this.content,
    required this.timestamp,
    this.colorHex = 0xFF1E293B,
  });

  Map<String, dynamic> toJson() => {
        'id': id,
        'title': title,
        'content': content,
        'timestamp': timestamp,
        'colorHex': colorHex,
      };

  factory NoteItem.fromJson(Map<String, dynamic> json) => NoteItem(
        id: json['id'] ?? '',
        title: json['title'] ?? '',
        content: json['content'] ?? '',
        timestamp: json['timestamp'] ?? 0,
        colorHex: json['colorHex'] ?? 0xFF1E293B,
      );
}

enum VirtualAppType {
  browser('المتصفح', Icons.language, Color(0xFF2563EB)),
  gallery('المعرض', Icons.photo_library, Color(0xFF9333EA)),
  notes('الملاحظات', Icons.edit_note, Color(0xFFD97706)),
  calculator('الحاسبة', Icons.calculate, Color(0xFF059669)),
  game('لعبة 2048', Icons.sports_esports, Color(0xFFE11D48)),
  settings('الإعدادات', Icons.settings, Color(0xFF475569)),
  clock('الساعة', Icons.schedule, Color(0xFF0284C7));

  final String title;
  final IconData icon;
  final Color color;
  const VirtualAppType(this.title, this.icon, this.color);
}

// ==================== Main Screen ====================
class VirtualPhoneScreen extends StatefulWidget {
  const VirtualPhoneScreen({super.key});

  @override
  State<VirtualPhoneScreen> createState() => _VirtualPhoneScreenState();
}

class _VirtualPhoneScreenState extends State<VirtualPhoneScreen> {
  SharedPreferences? _prefs;
  VirtualAppType? _currentApp;
  final List<VirtualAppType> _recentApps = [];
  bool _isRecentsOpen = false;
  bool _isNotificationShadeOpen = false;
  bool _showFrame = true;
  bool _isLocked = false;
  int _wallpaperIndex = 0;
  String _deviceName = "CloudDroid OS Pro";
  int _batteryPercent = 88;
  int _gameHighScore = 0;

  List<NoteItem> _notes = [];
  final List<String> _calcHistory = [];

  final List<LinearGradient> _wallpapers = const [
    LinearGradient(
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      colors: [Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF311042)],
    ),
    LinearGradient(
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
      colors: [Color(0xFF022C22), Color(0xFF064E3B), Color(0xFF065F46)],
    ),
    LinearGradient(
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      colors: [Color(0xFF431407), Color(0xFF7C2D12), Color(0xFF1E0A05)],
    ),
    LinearGradient(
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter,
      colors: [Color(0xFF0A0A0C), Color(0xFF1E293B), Color(0xFF050508)],
    ),
  ];

  @override
  void initState() {
    super.initState();
    _loadStoredData();
  }

  Future<void> _loadStoredData() async {
    _prefs = await SharedPreferences.getInstance();
    if (_prefs != null) {
      setState(() {
        _deviceName = _prefs!.getString('device_name') ?? "CloudDroid OS Pro";
        _wallpaperIndex = _prefs!.getInt('wallpaper_index') ?? 0;
        _showFrame = _prefs!.getBool('show_frame') ?? true;
        _batteryPercent = _prefs!.getInt('battery_percent') ?? 88;
        _gameHighScore = _prefs!.getInt('game_high_score') ?? 0;

        final notesJson = _prefs!.getString('saved_notes');
        if (notesJson != null) {
          final List decoded = jsonDecode(notesJson);
          _notes = decoded.map((e) => NoteItem.fromJson(e)).toList();
        } else {
          _notes = [
            NoteItem(
              id: '1',
              title: 'مرحباً في هاتفك السحابي',
              content: 'تطبيق هاتف وهمي مستقل لا يحل محل واجهة النظام الأساسية.',
              timestamp: DateTime.now().millisecondsSinceEpoch,
            )
          ];
        }
      });
    }
  }

  Future<void> _saveNotes() async {
    if (_prefs == null) return;
    final encoded = jsonEncode(_notes.map((e) => e.toJson()).toList());
    await _prefs!.setString('saved_notes', encoded);
  }

  Future<void> _saveSetting(String key, dynamic val) async {
    if (_prefs == null) return;
    if (val is String) await _prefs!.setString(key, val);
    if (val is int) await _prefs!.setInt(key, val);
    if (val is bool) await _prefs!.setBool(key, val);
  }

  void _openApp(VirtualAppType app) {
    setState(() {
      _currentApp = app;
      _isRecentsOpen = false;
      _isNotificationShadeOpen = false;
      if (!_recentApps.contains(app)) {
        _recentApps.insert(0, app);
      }
    });
  }

  void _goHome() {
    setState(() {
      _currentApp = null;
      _isRecentsOpen = false;
      _isNotificationShadeOpen = false;
    });
  }

  void _handleRealBackPress() {
    if (_isNotificationShadeOpen) {
      setState(() => _isNotificationShadeOpen = false);
    } else if (_isRecentsOpen) {
      setState(() => _isRecentsOpen = false);
    } else if (_currentApp != null) {
      _goHome();
    } else {
      _showExitDialog();
    }
  }

  void _showExitDialog() {
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: const Color(0xFF1E293B),
        title: const Text('الخروج من الهاتف الافتراضي'),
        content: const Text(
            'هل تريد العودة إلى شاشة جهازك الأصلية؟ جميع بياناتك وملاحظاتك محفوظة.'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text('إلغاء'),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            onPressed: () {
              Navigator.pop(ctx);
              SystemNavigator.pop();
            },
            child: const Text('خروج'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, result) {
        if (!didPop) _handleRealBackPress();
      },
      child: Scaffold(
        body: Center(
          child: Container(
            constraints: BoxConstraints(
              maxWidth: _showFrame ? 420 : double.infinity,
            ),
            margin: EdgeInsets.symmetric(
              vertical: _showFrame ? 16 : 0,
              horizontal: _showFrame ? 8 : 0,
            ),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(_showFrame ? 36 : 0),
              border: _showFrame
                  ? Border.all(color: const Color(0xFF475569), width: 3)
                  : null,
              boxShadow: _showFrame
                  ? [
                      const BoxShadow(
                        color: Colors.black54,
                        blurRadius: 20,
                        spreadRadius: 4,
                      )
                    ]
                  : null,
            ),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(_showFrame ? 33 : 0),
              child: Stack(
                children: [
                  // Wallpaper
                  Container(
                    decoration: BoxDecoration(
                      gradient: _wallpapers[_wallpaperIndex % _wallpapers.length],
                    ),
                  ),

                  // Phone UI
                  Column(
                    children: [
                      // Top Camera Notch if framed
                      if (_showFrame)
                        Container(
                          height: 18,
                          color: Colors.black,
                          child: Center(
                            child: Container(
                              width: 8,
                              height: 8,
                              decoration: const BoxDecoration(
                                color: Color(0xFF1E293B),
                                shape: BoxShape.circle,
                              ),
                            ),
                          ),
                        ),

                      // Status Bar
                      _buildStatusBar(),

                      // Active App / Home View
                      Expanded(
                        child: Stack(
                          children: [
                            if (_currentApp == null)
                              _buildHomeScreen()
                            else
                              _buildCurrentApp(),

                            // Recents Overlay
                            if (_isRecentsOpen) _buildRecentsOverlay(),

                            // Quick Shade Overlay
                            if (_isNotificationShadeOpen) _buildShadeOverlay(),
                          ],
                        ),
                      ),

                      // Bottom Virtual Navigation Bar
                      _buildNavBar(),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildStatusBar() {
    final timeStr = DateFormat('HH:mm').format(DateTime.now());
    return GestureDetector(
      onTap: () =>
          setState(() => _isNotificationShadeOpen = !_isNotificationShadeOpen),
      child: Container(
        height: 28,
        color: Colors.black38,
        padding: const EdgeInsets.symmetric(horizontal: 14),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              children: [
                Text(
                  timeStr,
                  style: const TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      color: Colors.white),
                ),
                const SizedBox(width: 8),
                Container(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 4, vertical: 1),
                  decoration: BoxDecoration(
                    color: Colors.lightBlue.withOpacity(0.3),
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: const Text('Cloud 12ms',
                      style: TextStyle(fontSize: 9, color: Colors.cyanAccent)),
                ),
              ],
            ),
            Row(
              children: [
                const Icon(Icons.wifi, size: 14, color: Colors.white),
                const SizedBox(width: 6),
                const Text('5G',
                    style: TextStyle(
                        fontSize: 10,
                        fontWeight: FontWeight.bold,
                        color: Colors.white)),
                const SizedBox(width: 6),
                Text('$_batteryPercent%',
                    style: const TextStyle(fontSize: 11, color: Colors.white)),
                const Icon(Icons.battery_full, size: 14, color: Colors.green),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildNavBar() {
    return Container(
      height: 48,
      color: Colors.black87,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          IconButton(
            icon: const Icon(Icons.arrow_back, color: Colors.white),
            onPressed: _handleRealBackPress,
          ),
          IconButton(
            icon: const Icon(Icons.radio_button_unchecked, color: Colors.white),
            onPressed: _goHome,
          ),
          IconButton(
            icon: const Icon(Icons.crop_square, color: Colors.white),
            onPressed: () {
              setState(() => _isRecentsOpen = !_isRecentsOpen);
            },
          ),
        ],
      ),
    );
  }

  Widget _buildHomeScreen() {
    final timeStr = DateFormat('HH:mm').format(DateTime.now());
    final dateStr = DateFormat('EEEE, MMM d').format(DateTime.now());

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          // Clock & Weather Widget
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: Colors.black38,
              borderRadius: BorderRadius.circular(20),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(timeStr,
                        style: const TextStyle(
                            fontSize: 32,
                            fontWeight: FontWeight.bold,
                            color: Colors.white)),
                    Text(dateStr,
                        style: const TextStyle(
                            fontSize: 12, color: Colors.white70)),
                  ],
                ),
                const Row(
                  children: [
                    Icon(Icons.wb_sunny, color: Colors.amber, size: 28),
                    SizedBox(width: 8),
                    Text('24°C',
                        style: TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                            color: Colors.white)),
                  ],
                ),
              ],
            ),
          ),

          // App Grid
          GridView.count(
            crossAxisCount: 4,
            shrinkWrap: true,
            mainAxisSpacing: 16,
            crossAxisSpacing: 10,
            physics: const NeverScrollableScrollPhysics(),
            children: VirtualAppType.values.map((app) {
              return GestureDetector(
                onTap: () => _openApp(app),
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      width: 50,
                      height: 50,
                      decoration: BoxDecoration(
                        color: app.color,
                        borderRadius: BorderRadius.circular(14),
                        boxShadow: const [
                          BoxShadow(color: Colors.black26, blurRadius: 4)
                        ],
                      ),
                      child: Icon(app.icon, color: Colors.white, size: 26),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      app.title,
                      style: const TextStyle(fontSize: 10, color: Colors.white),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                ),
              );
            }).toList(),
          ),

          // Bottom Dock
          Container(
            padding: const EdgeInsets.symmetric(vertical: 8, horizontal: 16),
            decoration: BoxDecoration(
              color: Colors.black45,
              borderRadius: BorderRadius.circular(24),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _buildDockIcon(Icons.call, Colors.green,
                    () => _openApp(VirtualAppType.notes)),
                _buildDockIcon(Icons.language, Colors.blue,
                    () => _openApp(VirtualAppType.browser)),
                _buildDockIcon(Icons.photo, Colors.purple,
                    () => _openApp(VirtualAppType.gallery)),
                _buildDockIcon(Icons.settings, Colors.blueGrey,
                    () => _openApp(VirtualAppType.settings)),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDockIcon(IconData icon, Color color, VoidCallback onTap) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: 44,
        height: 44,
        decoration: BoxDecoration(color: color, shape: BoxShape.circle),
        child: Icon(icon, color: Colors.white, size: 22),
      ),
    );
  }

  Widget _buildCurrentApp() {
    switch (_currentApp!) {
      case VirtualAppType.browser:
        return _buildBrowserApp();
      case VirtualAppType.gallery:
        return _buildGalleryApp();
      case VirtualAppType.notes:
        return _buildNotesApp();
      case VirtualAppType.calculator:
        return _buildCalculatorApp();
      case VirtualAppType.game:
        return _build2048GameApp();
      case VirtualAppType.settings:
        return _buildSettingsApp();
      case VirtualAppType.clock:
        return _buildClockApp();
    }
  }

  // ==================== Virtual Apps ====================
  Widget _buildNotesApp() {
    return Container(
      color: const Color(0xFF0F172A),
      child: Column(
        children: [
          AppBar(
            backgroundColor: const Color(0xFF1E293B),
            title: Text('الملاحظات (${_notes.length})'),
            actions: [
              IconButton(icon: const Icon(Icons.close), onPressed: _goHome),
            ],
          ),
          Expanded(
            child: _notes.isEmpty
                ? const Center(child: Text('لا توجد ملاحظات، اضغط +'))
                : ListView.builder(
                    itemCount: _notes.length,
                    padding: const EdgeInsets.all(12),
                    itemBuilder: (ctx, i) {
                      final n = _notes[i];
                      return Card(
                        color: Color(n.colorHex),
                        margin: const EdgeInsets.only(bottom: 8),
                        child: ListTile(
                          title: Text(n.title,
                              style:
                                  const TextStyle(fontWeight: FontWeight.bold)),
                          subtitle: Text(n.content),
                          trailing: IconButton(
                            icon: const Icon(Icons.delete, color: Colors.red),
                            onPressed: () {
                              setState(() => _notes.removeAt(i));
                              _saveNotes();
                            },
                          ),
                        ),
                      );
                    },
                  ),
          ),
          Padding(
            padding: const EdgeInsets.all(12),
            child: ElevatedButton.icon(
              onPressed: _showAddNoteDialog,
              icon: const Icon(Icons.add),
              label: const Text('إضافة ملاحظة جديدة'),
            ),
          ),
        ],
      ),
    );
  }

  void _showAddNoteDialog() {
    final titleCtrl = TextEditingController();
    final contentCtrl = TextEditingController();
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: const Color(0xFF1E293B),
        title: const Text('ملاحظة جديدة'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: titleCtrl,
              decoration: const InputDecoration(labelText: 'العنوان'),
            ),
            TextField(
              controller: contentCtrl,
              decoration: const InputDecoration(labelText: 'المحتوى'),
              maxLines: 3,
            ),
          ],
        ),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(ctx), child: const Text('إلغاء')),
          ElevatedButton(
            onPressed: () {
              if (titleCtrl.text.isNotEmpty || contentCtrl.text.isNotEmpty) {
                setState(() {
                  _notes.insert(
                    0,
                    NoteItem(
                      id: DateTime.now().millisecondsSinceEpoch.toString(),
                      title: titleCtrl.text.isEmpty
                          ? 'بدون عنوان'
                          : titleCtrl.text,
                      content: contentCtrl.text,
                      timestamp: DateTime.now().millisecondsSinceEpoch,
                    ),
                  );
                });
                _saveNotes();
              }
              Navigator.pop(ctx);
            },
            child: const Text('حفظ'),
          ),
        ],
      ),
    );
  }

  Widget _buildBrowserApp() {
    return Container(
      color: const Color(0xFF0F172A),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            color: const Color(0xFF1E293B),
            child: Row(
              children: [
                Expanded(
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12),
                    decoration: BoxDecoration(
                      color: const Color(0xFF0F172A),
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: const Row(
                      children: [
                        Icon(Icons.language, size: 16, color: Colors.blue),
                        SizedBox(width: 8),
                        Text('https://cloud-browser.internal',
                            style: TextStyle(fontSize: 12)),
                      ],
                    ),
                  ),
                ),
                IconButton(icon: const Icon(Icons.close), onPressed: _goHome),
              ],
            ),
          ),
          const Expanded(
            child: Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.cloud_done, size: 48, color: Colors.blue),
                  SizedBox(height: 12),
                  Text('متصفح الويب السحابي الآمن',
                      style:
                          TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                  Text('جميع الروابط مشفرة ومعزولة داخل الهاتف الوهمي.',
                      style: TextStyle(color: Colors.grey)),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildGalleryApp() {
    return Container(
      color: const Color(0xFF0F172A),
      child: Column(
        children: [
          AppBar(
            backgroundColor: const Color(0xFF1E293B),
            title: const Text('معرض الصور والخلفيات'),
            actions: [
              IconButton(icon: const Icon(Icons.close), onPressed: _goHome),
            ],
          ),
          Expanded(
            child: GridView.builder(
              padding: const EdgeInsets.all(12),
              gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2,
                crossAxisSpacing: 10,
                mainAxisSpacing: 10,
              ),
              itemCount: _wallpapers.length,
              itemBuilder: (ctx, i) {
                return GestureDetector(
                  onTap: () {
                    setState(() => _wallpaperIndex = i);
                    _saveSetting('wallpaper_index', i);
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                          content: Text('تم تعيينها كخلفية للهاتف الوهمي!')),
                    );
                  },
                  child: Container(
                    decoration: BoxDecoration(
                      gradient: _wallpapers[i],
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Center(
                      child: Text(
                        'خلفية ${i + 1}\n(اضغط للتعيين)',
                        textAlign: TextAlign.center,
                        style: const TextStyle(fontWeight: FontWeight.bold),
                      ),
                    ),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCalculatorApp() {
    return Container(
      color: const Color(0xFF0F172A),
      padding: const EdgeInsets.all(16),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text('الحاسبة',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              IconButton(icon: const Icon(Icons.close), onPressed: _goHome),
            ],
          ),
          const Spacer(),
          const Text('0',
              style: TextStyle(fontSize: 48, fontWeight: FontWeight.bold)),
          const SizedBox(height: 20),
          const Text('حاسبة سحابية متكاملة وسريعة',
              style: TextStyle(color: Colors.grey)),
          const SizedBox(height: 20),
        ],
      ),
    );
  }

  Widget _build2048GameApp() {
    return Container(
      color: const Color(0xFF0F172A),
      child: Column(
        children: [
          AppBar(
            backgroundColor: const Color(0xFF1E293B),
            title: Text('لعبة 2048 (أفضل نتيجة: $_gameHighScore)'),
            actions: [
              IconButton(icon: const Icon(Icons.close), onPressed: _goHome),
            ],
          ),
          const Expanded(
            child: Center(
              child: Text('اسحب على الشاشة لتحريك المربعات ودمج الأرقام!'),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSettingsApp() {
    return Container(
      color: const Color(0xFF0F172A),
      child: Column(
        children: [
          AppBar(
            backgroundColor: const Color(0xFF1E293B),
            title: const Text('الإعدادات'),
            actions: [
              IconButton(icon: const Icon(Icons.close), onPressed: _goHome),
            ],
          ),
          Expanded(
            child: ListView(
              padding: const EdgeInsets.all(16),
              children: [
                SwitchListTile(
                  title: const Text('وضع إطار الهاتف الذكي'),
                  subtitle: const Text('إظهار إطار الجهاز الخارجي'),
                  value: _showFrame,
                  onChanged: (v) {
                    setState(() => _showFrame = v);
                    _saveSetting('show_frame', v);
                  },
                ),
                ListTile(
                  title: const Text('اسم الجهاز الافتراضي'),
                  subtitle: Text(_deviceName),
                  trailing: const Icon(Icons.edit),
                  onTap: () {
                    // edit name
                  },
                ),
                ListTile(
                  title: const Text('حالة السحابة'),
                  subtitle: const Text('Cloud Node EU (12ms Latency)'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildClockApp() {
    final now = DateTime.now();
    return Container(
      color: const Color(0xFF0F172A),
      child: Column(
        children: [
          AppBar(
            backgroundColor: const Color(0xFF1E293B),
            title: const Text('الساعة'),
            actions: [
              IconButton(icon: const Icon(Icons.close), onPressed: _goHome),
            ],
          ),
          Expanded(
            child: Center(
              child: Text(
                DateFormat('HH:mm:ss').format(now),
                style:
                    const TextStyle(fontSize: 48, fontWeight: FontWeight.bold),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildRecentsOverlay() {
    return Container(
      color: Colors.black87,
      child: Column(
        children: [
          const SizedBox(height: 20),
          const Text('التطبيقات المفتوحة في الخلفية',
              style: TextStyle(fontWeight: FontWeight.bold)),
          Expanded(
            child: _recentApps.isEmpty
                ? const Center(child: Text('لا توجد تطبيقات نشطة'))
                : ListView.builder(
                    scrollDirection: Axis.horizontal,
                    itemCount: _recentApps.length,
                    padding: const EdgeInsets.all(24),
                    itemBuilder: (ctx, i) {
                      final a = _recentApps[i];
                      return GestureDetector(
                        onTap: () => _openApp(a),
                        child: Container(
                          width: 160,
                          margin: const EdgeInsets.only(right: 16),
                          decoration: BoxDecoration(
                            color: const Color(0xFF1E293B),
                            borderRadius: BorderRadius.circular(16),
                          ),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(a.icon, size: 40, color: a.color),
                              const SizedBox(height: 8),
                              Text(a.title,
                                  style: const TextStyle(
                                      fontWeight: FontWeight.bold)),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
          ),
          ElevatedButton(
            onPressed: () => setState(() {
              _recentApps.clear();
              _isRecentsOpen = false;
              _currentApp = null;
            }),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('إغلاق الكل'),
          ),
          const SizedBox(height: 20),
        ],
      ),
    );
  }

  Widget _buildShadeOverlay() {
    return Container(
      color: const Color(0xF00F172A),
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text('مركز التحكم والإشعارات',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
              IconButton(
                icon: const Icon(Icons.close),
                onPressed: () =>
                    setState(() => _isNotificationShadeOpen = false),
              ),
            ],
          ),
          const SizedBox(height: 16),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              _buildShadeTile(Icons.wifi, 'WiFi', true),
              _buildShadeTile(Icons.bluetooth, 'Bluetooth', true),
              _buildShadeTile(Icons.airplanemode_active, 'Flight', false),
              _buildShadeTile(Icons.flash_on, 'Flashlight', false),
            ],
          ),
          const Spacer(),
          const Text('لا توجد إشعارات جديدة',
              style: TextStyle(color: Colors.grey)),
          const Spacer(),
        ],
      ),
    );
  }

  Widget _buildShadeTile(IconData icon, String label, bool active) {
    return Column(
      children: [
        Container(
          width: 50,
          height: 50,
          decoration: BoxDecoration(
            color: active ? Colors.blue : const Color(0xFF1E293B),
            borderRadius: BorderRadius.circular(12),
          ),
          child: Icon(icon, color: Colors.white),
        ),
        const SizedBox(height: 4),
        Text(label, style: const TextStyle(fontSize: 10)),
      ],
    );
  }
}
