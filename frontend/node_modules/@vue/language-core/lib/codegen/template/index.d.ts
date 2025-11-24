import type * as ts from 'typescript';
import type { Code, Sfc, VueCompilerOptions } from '../../types';
export interface TemplateCodegenOptions {
    ts: typeof ts;
    compilerOptions: ts.CompilerOptions;
    vueCompilerOptions: VueCompilerOptions;
    template: NonNullable<Sfc['template']>;
    scriptSetupBindingNames: Set<string>;
    scriptSetupImportComponentNames: Set<string>;
    destructuredPropNames: Set<string>;
    templateRefNames: Set<string>;
    hasDefineSlots?: boolean;
    propsAssignName?: string;
    slotsAssignName?: string;
    inheritAttrs: boolean;
    selfComponentName?: string;
}
export { generate as generateTemplate };
declare function generate(options: TemplateCodegenOptions): {
    codes: Code[];
    currentInfo: {
        ignoreError?: boolean;
        expectError?: {
            token: number;
            node: import("@vue/compiler-dom").CommentNode;
        };
        generic?: {
            content: string;
            offset: number;
        };
    };
    resolveCodeFeatures: (features: import("../../types").VueCodeInformation) => import("../../types").VueCodeInformation;
    inlineTsAsts: Map<string, ts.SourceFile> | undefined;
    inVFor: boolean;
    slots: {
        name: string;
        offset?: number;
        tagRange: [number, number];
        nodeLoc: any;
        propsVar: string;
    }[];
    dynamicSlots: {
        expVar: string;
        propsVar: string;
    }[];
    dollarVars: Set<string>;
    accessExternalVariables: Map<string, Set<number>>;
    blockConditions: string[];
    scopedClasses: {
        source: string;
        className: string;
        offset: number;
    }[];
    emptyClassOffsets: number[];
    inlayHints: import("../inlayHints").InlayHintInfo[];
    bindingAttrLocs: import("@vue/compiler-dom").SourceLocation[];
    inheritedAttrVars: Set<string>;
    templateRefs: Map<string, {
        typeExp: string;
        offset: number;
    }[]>;
    currentComponent: {
        ctxVar: string;
        used: boolean;
    } | undefined;
    singleRootElTypes: string[];
    singleRootNodes: Set<import("@vue/compiler-dom").ElementNode | null>;
    addTemplateRef(name: string, typeExp: string, offset: number): void;
    accessExternalVariable(name: string, offset?: number): void;
    hasLocalVariable(name: string): boolean;
    addLocalVariable(name: string): void;
    removeLocalVariable(name: string): void;
    getInternalVariable(): string;
    getHoistVariable(originalVar: string): string;
    generateHoistVariables(): Generator<string, void, unknown>;
    generateConditionGuards(): Generator<string, void, unknown>;
    generateAutoImportCompletion(): Generator<Code>;
    enter(node: import("@vue/compiler-dom").RootNode | import("@vue/compiler-dom").TemplateChildNode | import("@vue/compiler-dom").SimpleExpressionNode): boolean;
    exit(): Generator<Code>;
};
