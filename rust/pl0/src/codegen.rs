use std::collections::HashMap;
use std::convert::TryInto;
use crate::ir::{IR, Line};

#[derive(Default)]
pub struct CodeGen {
    symbol_table: HashMap<String, u32>,
    pub output: Vec<u32>,
    address: u32,
}

impl CodeGen {
    pub fn new() -> CodeGen {
        let symbol_table = HashMap::new();
        let output = Vec::new();
        let address = 0;
        CodeGen {
            symbol_table,
            output,
            address
        }
    }

    pub fn gen(&mut self, input: &mut Vec<Line>) {
        // Relocate data reserved for variables.
        let mut reorder = Vec::new();
        let mut i = 0;
        while i < input.len() {
            let line = input[i].clone();
            if let Line { inst: IR::DEC(_), .. } = line {
                reorder.push(line);
                input.remove(i);
            } else {
                i += 1;
            }
        }
        input.append(&mut reorder);

        // Relocate functions.
        let mut functions = Vec::new();
        i = 0;
        let mut in_func = false;
        while i < input.len() {
            let line = input[i].clone();
            if let Line { inst: IR::StartFunc, .. } = line {
                in_func = true;
            }

            if in_func {
                functions.push(line.clone());
                input.remove(i);
            } else {
                i += 1;
            }

            if let Line { inst: IR::RET, .. } = line {
                in_func = false;
            }
        }
        input.append(&mut functions);

        // Gather addresses of symbols.
        for i in input.iter() {
            let label = i.label.clone();

            if label.is_some() {
                let label = label.unwrap();
                self.symbol_table.insert(label, self.address);
            }

            self.address += 1;
        }

        for i in input.iter() {
            let inst = i.inst.clone();
            match inst {
                IR::JMP(l) => {
                    let jmp_addr = self.symbol_table.get(&l);
                    self.output.push(0x1000_0000 | jmp_addr.unwrap());
                },
                IR::JMZ(l) => {
                    let jmp_addr = self.symbol_table.get(&l);
                    self.output.push(0x2000_0000 | jmp_addr.unwrap());
                },
                IR::LOAD(l) => {
                    let load_addr = self.symbol_table.get(&l);
                    self.output.push(0x3000_0000 | load_addr.unwrap());
                },
                IR::LOADC(n) => {
                    self.output.push((0x4000_0000 | n).try_into().unwrap());
                },
                IR::STORE(l) => {
                    let store_addr = self.symbol_table.get(&l);
                    self.output.push(0x5000_0000 | store_addr.unwrap());
                },
                IR::CALL(l) => {
                    let func_addr = self.symbol_table.get(&l);
                    self.output.push(0x6000_0000 | func_addr.unwrap());
                },
                IR::WRITE => {
                    self.output.push(0x7000_0000);
                },
                IR::ADD => {
                    self.output.push(0x8000_0000);
                },
                IR::SUB => {
                    self.output.push(0x9000_0000);
                },
                IR::DIV => {
                    self.output.push(0xA000_0000);
                },
                IR::MUL => {
                    self.output.push(0xB000_0000);
                },
                IR::ODD => {
                    self.output.push(0xC000_0000);
                },
                IR::LT => {
                    self.output.push(0xD000_0000);
                },
                IR::LTE => {
                    self.output.push(0xE000_0000);
                },
                IR::GT => {
                    self.output.push(0xF000_0000);
                },
                IR::GTE => {
                    self.output.push(0xF100_0000);
                },
                IR::EQ => {
                    self.output.push(0xF200_0000);
                },
                IR::NEQ => {
                    self.output.push(0xF300_0000);
                },
                IR::NOOP => {
                    self.output.push(0xF400_0000);
                },
                IR::StartFunc => {
                    self.output.push(0xF400_0000);
                },
                IR::RET => {
                    self.output.push(0xF500_0000);
                },
                IR::HALT => {
                    self.output.push(0xF600_0000);
                },
                IR::DEC(n) => {
                    self.output.push(n.try_into().unwrap());
                },
            }
        }
    }
}
