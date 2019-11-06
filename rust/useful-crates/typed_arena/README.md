如果使用称为Typed Arena的库，则可以多次分配具有相同生存期的内存。

Typed Arena，arena crates --- arena::TypedArena和typed_arena crate  --- typed_arena::Arena存在。这些几乎相同，但是arena只能在编译器内部使用，并且通常仅在nightly可用。一般情况使用typed_arena。

包含交叉引用的数据结构存在两个问题，每个问题都有一个解决方案。

- 在某些情况下，需要在维护多个引用的同时重写数据。使用→ RefCell。
- 想准备多个具有相同生存期的数据，以供彼此参考。如果想一开始就固定好，可用Vec等等。如果要动态分配，请使用typed_arena::Arena（或arena::TypedArena）。
TypedArena在Rust 编译器中也使用。例如，导入解决方案TypedArena用于处理graph。


```
[dependencies]
typed-arena = "1.7.0"
```


```
extern crate typed_arena;

use std::cell::RefCell;
use typed_arena::Arena;

struct NodeData<'a> {
    references: RefCell<Vec<Node<'a>>>
}
type Node<'a> = &'a NodeData<'a>;

//如添加Drop实现，它还是一个编译错误。
//编译此示例时怎么了？这与以下事实有关：首先调用节点0或节点1的析构函数。
//假设在node0之前销毁了node1。在这种情况下，当调用节点0的析构函数时，节点1的析构过程完成。但是，node0有一个指向node1的指针，因此它可以在node0的析构函数中搜索node1的废墟。当然，其中许多都是无效数据，当触摸它们时可能会爆炸。
/*
impl<'a> Drop<'a> for NodeData<'a> {
    fn drop(&mut self) {}
}
*/

fn main() {
    let nodes = Arena::new(); 
    let node0 = nodes.alloc(NodeData { references: RefCell::new(vec![]) });
    node0.references.borrow_mut().push(node0);
    let node1 = nodes.alloc(NodeData { references: RefCell::new(vec![]) });
    node0.references.borrow_mut().push(node1);
}

```
