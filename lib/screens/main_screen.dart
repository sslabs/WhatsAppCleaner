import 'package:flutter/material.dart';
import 'package:whatsapp_cleaner/cards/scheduling_card.dart';
import 'package:whatsapp_cleaner/pages/cards_page.dart';
import 'package:whatsapp_cleaner/pages/scheduling_page.dart';

class MainScreen extends StatefulWidget {
  final String title;

  MainScreen({@required this.title, Key key}) : super(key: key);

  @override
  _MainScreenState createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _selectedPageIndex = 0;

  final pageController = PageController(initialPage: 0, keepPage: true);

  final _bottomItems = [
    BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
    BottomNavigationBarItem(icon: Icon(Icons.schedule), label: 'Schedule')
  ];

  @override
  Widget build(BuildContext context) {
    final pages = [
      CardsPage(cards: [SchedulingCard(action: () => _goToPage(1))]),
      SchedulingPage()
    ];

    return Scaffold(
      appBar: AppBar(title: Text(widget.title)),
      body: PageView(
        controller: pageController,
        onPageChanged: (index) => setState(() {
          _selectedPageIndex = index;
        }),
        children: pages,
        physics: NeverScrollableScrollPhysics(),
      ),
      bottomNavigationBar: BottomNavigationBar(
        items: _bottomItems,
        currentIndex: _selectedPageIndex,
        onTap: (index) {
          setState(() {
            _selectedPageIndex = index;
          });
          _goToPage(index);
        },
      ),
    );
  }

  void _goToPage(int index) {
    pageController.animateToPage(index,
        duration: Duration(milliseconds: 500), curve: Curves.ease);
  }
}
