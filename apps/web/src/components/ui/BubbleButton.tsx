import { twMerge } from "tailwind-merge";
import { ReactNode } from "react";

type ButtonProps = {
	children: ReactNode;
	className?: string;
	onClick?: () => void;
} & React.ButtonHTMLAttributes<HTMLButtonElement>;

export const BubbleButton = ({
	children,
	className,
	onClick,
	...rest
}: ButtonProps) => {
	return (
		<button
			onClick={onClick}
			className={twMerge(
				`
        relative z-0 flex items-center gap-2 overflow-hidden whitespace-nowrap rounded-md 
        border border-orange-700 bg-gradient-to-br from-likeme-primary to-likeme-accent
        px-3 py-1.5
        text-zinc-50 transition-all duration-300
        
        before:absolute before:inset-0
        before:-z-10 before:translate-y-[200%]
        before:scale-[2.5]
        before:rounded-[100%] before:bg-zinc-100
        before:transition-transform before:duration-500
        before:content-[""]

        hover:scale-105 hover:text-zinc-900
        hover:before:translate-y-[0%]
        active:scale-100`,
				className
			)}
			{...rest}
		>
			{children}
		</button>
	);
};

export default BubbleButton;
