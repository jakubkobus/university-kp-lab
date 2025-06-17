#pragma once
#include <string>
#include <memory>
#include <sstream>

template <typename T>
class BinaryTree {
  struct Node
  {
    T value;
    std::unique_ptr<Node> left, right;
    Node(const T &v);
  };

  std::unique_ptr<Node> root;
  const int NODE_WIDTH = 8;

  Node *insertRec(Node *node, const T &value);
  bool searchRec(Node *node, const T &value) const;
  Node *deleteRec(Node *node, const T &value);
  void drawRec(const Node *node, std::ostringstream &oss, int depth) const;
  int sizeRec(const Node *node) const;

public:
  BinaryTree();
  void insert(const T &value);
  bool search(const T &value) const;
  bool remove(const T &value);
  std::string draw() const;
  int size() const;
  bool isEmpty() const;
};