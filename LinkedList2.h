#ifndef LINKEDLIST_H
#define LINKEDLIST_H

#include <iostream>
#include <string>
#include <iostream>
#include <fstream>
#include <cstdlib>

#include <sstream>

using namespace std;


class Node
{
  public:
   Node();

   Node( string* p_tags, int p_size, int p_count);
   
   void countpp();
   
   void setNext( Node* node );
   
   string* getTags();
   int getCount();
   Node* getNext();
   int getSize();

  private:
   Node* next;
   string* tags;
   int size;
   int count;
   
};

class LinkedList
{
  public:
   LinkedList( int p_size);

   void insert ( string* tags );
   
   void insert ( Node* curr, Node* node );
   
   bool write();
   
   bool nodeCompare( Node* node1, Node* node2);
   
   unsigned int getTotalOccur();
   unsigned int getTypes();
   
  private:
   Node* head;
   int nodeSize;
   unsigned int totalOccur;
   unsigned int types;
   
};




#endif
